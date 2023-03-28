package com.yuanm.funneytest.imagevideo

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLExt
import android.util.Log
import android.view.Surface

/**
 * Android平台下，EGL环境搭建，java层代码实现
 * EGL是OpenGL ES渲染API和本地窗口系统之间的一个中间接口层，它主要由系统制造商实现。EGL主要有以下作用：
 * 与设备的原生窗口系统通信
 * 查询绘图表面的可用类型和配置
 * 创建绘图表面
 * 在OpenGL ES和其他图形渲染API之间同步渲染
 * 管理纹理贴图等渲染资源
 *
 * EGL的几个关键类
 * Display(EGLDisplay)是对实际显示设备的抽象
 * Surface(EGLSurface)是对用来存储图像的内存区域FrameBuffer的抽象，包括Color Buffer（颜色缓冲区）,Stencil Buffer（模板缓冲区）,Depth Buffer（深度缓冲区）
 * Context(EGLContext)存储OpenGL ES绘图的一些状态信息
 *
 * 使用EGL绘图的基本步骤
 * 1.获取EGLDisplay对象，建立与本地窗口系统的连接：eglGetDisplay()
 * 2.成功打开连接之后，需要初始化EGL：eglInitialize()
 * 3.获取EGLConfig对象，确定渲染表面的配置信息：eglChooseConfig()
 * 4.创建渲染表面EGLSurface实例：eglCreateWindowSurface()
 * 5.创建EGLContext实例：eglCreateContext()
 * 6.绑定EGLContext和EGLSurface：eglMakeCurrent()
 * 7.使用OpenGL ES API绘制图形
 * 8.切换front buffer和back buffer送显：eglSwapBuffer()
 * 9.断开并释放与EGLSurface关联的EGLContext对象：eglRelease()
 * 10.删除EGLSurface对象
 * 11.删除EGLContext对象
 * 12.终止与EGLDisplay之间的连接
 *
 * EGL用法参考博客：https://www.cnblogs.com/wellcherish/p/12727906.html
 *
 */
class EglEnv(val width: Int, val height: Int) {

  private val TAG = "EglEnv"

  private var eglDisplay = EGL14.EGL_NO_DISPLAY
  private var eglContext = EGL14.EGL_NO_CONTEXT
  private var eglSurface = EGL14.EGL_NO_SURFACE

  private var eglConfig: EGLConfig? = null


  fun setUpEglEnv(surface: Surface) {
    /**
     * 第一步：获取EGLDisplay对象，建立与本地窗口系统的连接
     * 在EGL能够确定可用的绘制表面类型之前，它必须打开和窗口系统的通信渠道。
     * 因为每个窗口系统都有不同的语义，所以EGL提供了基本的不对操作系统透明的类型---EGLDisplay。
     * 该类型封装了所有系统相关性，用于和原生窗口系统交流。
     * 任何使用EGL的应用程序，第一个操作必须是创建和初始化与本地EGL显示的连接。
     */
    eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY) // 获取EGLDisplay对象，建立与本地窗口系统的连接
    if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
      checkEglError("can't load EGL display")
    }

    /**
     * 第二步：初始化EGL
     * 二维数组用于存放获取到的版本号，主版本号放在version[0]，次版本号放在version[1]，两个版本号可能为空
     */
    val version = IntArray(2)
    if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
      checkEglError("EGL initialize failed")
    }

    // 第三步：确定渲染表面的配置
    // 设置指定的属性列表，格式为：属性名，默认值，并以EGL14.EGL_NONE结束
    val attrib_list =
      intArrayOf(
        EGL14.EGL_BUFFER_SIZE, 32, // 颜色缓冲区中所有颜色分量的位数，其值是 EGL_RED_SIZE, EGL_GREEN_SIZE, EGL_BLUE_SIZE, 和 EGL_ALPHA_SIZE 的位数之和
        EGL14.EGL_BLUE_SIZE, 8, // 颜色缓冲区中蓝色分量的位数
        EGL14.EGL_GREEN_SIZE, 8, // 颜色缓冲区中绿色分量的位数
        EGL14.EGL_RED_SIZE, 8, // 颜色缓冲区中红色分量的位数
        EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, // 位掩码，代表 EGL 支持的渲染类型的接口。由 EGL_OPENGL_ES_BIT、EGL_OPENGL_ES2_BIT、EGL_OPENGL_ES3 _BIT_KHR(需要 EGL_KHR_create_context 扩展)、EGL_OPENGL_BIT 或 EGL_OPENVG_BIT 组成
        EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT, // 支持的 EGL 表面类型, 可能是：EGL_WINDOW_BIT、EGL_PIXMAP_BIT、EGL_PBUFFER_BIT、EGL_MULTISAMPLE_RESOLVE_BOX_BIT、EGL_SWAP_BEHAVIOR_PRESERVED_BIT、EGL_VG_COLORSPACE_LINEAR_BIT 或 EGL_VG_ALPHA_FORMAT_PRE_BIT
        // 属性定义结束
        EGL14.EGL_NONE)
    // 用于存放获取到的EGLConfig对象
    val configs = arrayOfNulls<EGLConfig>(1)
    val num_config = IntArray(1)
    /**
     * 获取EGLConfig对象，确定渲染表面的配置信息
     * attrib_list：指定待查询的EGLConfig匹配的属性列表
     * attrib_listOffset：属性列表的取值位移
     * configs：EGLConfig的配置列表
     * configsOffset：配置的取值偏移
     * config_size：配置列表的尺寸
     * num_config：指定返回的配置大小，数组长度一般设置为1即可
     * num_configOffset：取值偏移0
     */
    if (!EGL14.eglChooseConfig(eglDisplay, attrib_list, 0, configs, 0, configs.size, num_config, 0)) {
      checkEglError("EGL choose config failed")
    }
    eglConfig = configs[0]
    val format = IntArray(1)
    // 查询配置的特定信息
    if (!EGL14.eglGetConfigAttrib(eglDisplay, eglConfig, EGL14.EGL_NATIVE_VISUAL_ID, format, 0)) {
      checkEglError("EGL getConfig attrib failed")
    }


    // 第四步：创建渲染表面EGLSurface实例
    if (eglSurface != EGL14.EGL_NO_SURFACE) {
      throw RuntimeException("EGL already config surface")
    }
    val surfaceAttribs = intArrayOf(EGL14.EGL_NONE)
    // 创建渲染表面EGLSurface，调用eglCreateWindowSurface 或 eglCreatePbufferSurface 方法创建渲染表面，得到EGLSurface，其中
    // eglCreateWindowSurface 用于创建屏幕上渲染区域，eglCreatePbufferSurface 用于创建屏幕外渲染区域
    eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0)
    if (eglSurface == EGL14.EGL_NO_SURFACE) {
      checkEglError("EGL create window surface failed")
    }

    // 第五步：创建渲染上下文EGLContext实例
    val attributes = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
    // 渲染上下文是OpenGL ES的内部数据结构，包含操作所需的所有状态信息。在OpenGL ES中，必须要有个上下文才能绘图
    eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, attributes, 0)
    if (eglContext == EGL14.EGL_NO_CONTEXT) {
      checkEglError("EGL create context failed")
    }

    /**
     * 第六步：绑定EGLContext和EGLSurface
     * 通过eglMakeCurrent方法将EGLSurface、EGLContext、EGLDisplay三者绑定，绑定成功之后OpenGLES环境就创建好了，接下来便可以进行渲染
     */
    Log.d(TAG, " egl make current ")
    if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
      checkEglError("EGL make current failed")
    }
  }

  /**
   * @param nsecs 纳秒
   */
  fun setPresentationTime(nsecs: Long) {
    EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, nsecs)
    checkEglError("eglPresentationTimeANDROID")
  }

  /**
   * EGL是双缓冲机制，Back Frame Buffer 和 Front Frame Buffer，正常绘制目标都是Back Frame Buffer
   * 将绘制完毕的FrameBuffer交换到Front Frame Buffer并显示出来
   */
  fun swapBuffers(): Boolean {
    val result = EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    checkEglError("eglSwapBuffers")
    return result
  }

  fun release() {
    if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
      EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
      EGL14.eglDestroySurface(eglDisplay, eglSurface)
      EGL14.eglDestroyContext(eglDisplay, eglContext)
      EGL14.eglReleaseThread()
      EGL14.eglTerminate(eglDisplay)
    }
    eglSurface = EGL14.EGL_NO_SURFACE
    eglContext = EGL14.EGL_NO_CONTEXT
    eglDisplay = EGL14.EGL_NO_DISPLAY
  }

  private fun checkEglError(msg: String) {
    val error: Int = EGL14.eglGetError()
    val errorMsg = when (error) {
      EGL14.EGL_SUCCESS -> "函数执行成功，无错误---没有错误"
      EGL14.EGL_NOT_INITIALIZED -> "对于特定的 Display, EGL 未初始化，或者不能初始化---没有初始化"
      EGL14.EGL_BAD_ACCESS -> "EGL 无法访问资源(如 Context 绑定在了其他线程)---访问失败"
      EGL14.EGL_BAD_ALLOC -> "对于请求的操作，EGL 分配资源失败---分配失败"
      EGL14.EGL_BAD_ATTRIBUTE -> "未知的属性，或者属性已失效---错误的属性"
      EGL14.EGL_BAD_CONTEXT -> "EGLContext(上下文) 错误或无效---错误的上下文"
      EGL14.EGL_BAD_CONFIG -> "EGLConfig(配置) 错误或无效---错误的配置"
      EGL14.EGL_BAD_DISPLAY -> "EGLDisplay(显示) 错误或无效---错误的显示设备对象"
      EGL14.EGL_BAD_SURFACE -> "未知的属性，或者属性已失效---错误的Surface对象"
      EGL14.EGL_BAD_CURRENT_SURFACE -> "窗口，缓冲和像素图(三种 Surface)的调用线程的 Surface 错误或无效---当前Surface对象错误"
      EGL14.EGL_BAD_MATCH -> "参数不符(如有效的 Context 申请缓冲，但缓冲不是有效的 Surface 提供)---无法匹配"
      EGL14.EGL_BAD_PARAMETER -> "错误的参数"
      EGL14.EGL_BAD_NATIVE_PIXMAP -> "NativePixmapType 对象未指向有效的本地像素图对象---错误的像素图"
      EGL14.EGL_BAD_NATIVE_WINDOW -> "NativeWindowType 对象未指向有效的本地窗口对象---错误的本地窗口对象"
      EGL14.EGL_CONTEXT_LOST -> "电源错误事件发生，Open GL重新初始化，上下文等状态重置---上下文丢失"
      else -> "其它错误: 0x" + Integer.toHexString(error)
    }
    if (error != EGL14.EGL_SUCCESS) {
      throw RuntimeException(msg + errorMsg)
    }
  }

}
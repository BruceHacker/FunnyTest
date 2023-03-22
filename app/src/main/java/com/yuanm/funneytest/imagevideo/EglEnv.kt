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
 * 2.初始化EGL方法：eglInitialize()
 * 3.获取EGLConfig对象，确定渲染表面的配置信息：eglChooseConfig()
 * 4.创建EGLContext实例：eglCreateContext()
 * 5.创建EGLSurface实例：eglCreateWindowSurface()
 * 6.连接EGLContext和EGLSurface：eglMakeCurrent()
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

  private var eglDisplay = EGL14.EGL_NO_DISPLAY
  private var eglContext = EGL14.EGL_NO_CONTEXT
  private var eglSurface = EGL14.EGL_NO_SURFACE

  private var eglConfig: EGLConfig? = null


  fun setUpEnv(): EglEnv {
    // 获取EGLDisplay对象，建立与本地窗口系统的连接
    eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
      checkEglError("can't load EGL display")
    }
    val version = IntArray(2)
    // 初始化EGL方法
    if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
      checkEglError("EGL initialize failed")
    }
    val attribs =
      intArrayOf(EGL14.EGL_BUFFER_SIZE, 32, EGL14.EGL_BLUE_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_RED_SIZE, 8,
        EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
        EGL14.EGL_NONE)
    val configs = arrayOfNulls<EGLConfig>(1)
    val numConfigs = IntArray(1)
    // 获取EGLConfig对象，确定渲染表面的配置信息
    if (!EGL14.eglChooseConfig(eglDisplay, attribs, 0, configs, 0, configs.size, numConfigs, 0)) {
      checkEglError("EGL choose config failed")
    }
    eglConfig = configs[0]
    val attributes = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
    // 创建EGLContext实例
    eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, attributes, 0)
    if (eglContext == EGL14.EGL_NO_CONTEXT) {
      checkEglError("EGL create context failed")
    }
    return this
  }

  fun buildWindowsSurface(surface: Surface): EglEnv {
    val format = IntArray(1)
    if (!EGL14.eglGetConfigAttrib(eglDisplay, eglConfig, EGL14.EGL_NATIVE_VISUAL_ID, format, 0)) {
      checkEglError("EGL getConfig attrib failed")
    }
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
    // 通过eglMakeCurrent方法将EGLSurface、EGLContext、EGLDisplay三者绑定，绑定成功之后OpenGLES环境就创建好了，接下来便可以进行渲染
    Log.d(this.javaClass.name, " egl make current ")
    if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
      checkEglError("EGL make current failed")
    }
    return this
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


  // todo: 可抽象成扩展方法
  private fun checkEglError(msg: String) {
    val error: Int = EGL14.eglGetError()
    if (error != EGL14.EGL_SUCCESS) {
      throw RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error))
    }
  }

}
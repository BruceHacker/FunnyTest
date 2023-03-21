package com.yuanm.funneytest.imagevideo

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.util.Log
import android.view.Surface

/**
 * Android平台下，EGL环境搭建，java层代码实现
 */
class EglEnv(val width: Int, val height: Int) {

  private var eglDisplay = EGL14.EGL_NO_DISPLAY
  private var eglContext = EGL14.EGL_NO_CONTEXT
  private var eglSurface = EGL14.EGL_NO_SURFACE

  private var eglConfig: EGLConfig? = null


  fun setUpEnv(): EglEnv {
    // 构建一个显示设备
    eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
      checkEglError("can't load EGL display")
    }
    val version = IntArray(2)
    if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
      checkEglError("EGL initialize failed")
    }
    val attribs =
      intArrayOf(EGL14.EGL_BUFFER_SIZE, 32, EGL14.EGL_BLUE_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_RED_SIZE, 8,
        EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
        EGL14.EGL_NONE)
    val configs = arrayOfNulls<EGLConfig>(1)
    val numConfigs = IntArray(1)
    if (!EGL14.eglChooseConfig(eglDisplay, attribs, 0, configs, 0, configs.size, numConfigs, 0)) {
      checkEglError("EGL choose config failed")
    }
    eglConfig = configs[0]
    // 构建上下文环境
    val attributes = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
    // share_context 是否与其他上下文共享OpenGL资源
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
    eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0)
    if (eglSurface == EGL14.EGL_NO_SURFACE) {
      checkEglError("EGL create window surface failed")
    }
    makeCurrent()
    return this
  }

  /**
   * 为此线程绑定上下文
   * */
  private fun makeCurrent() {
    Log.d(this.javaClass.name, " egl make current ")
    if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
      checkEglError("EGL make current failed")
    }
  }


  // todo: 可抽象成扩展方法
  private fun checkEglError(msg: String) {
    val error: Int = EGL14.eglGetError()
    if (error != EGL14.EGL_SUCCESS) {
      throw RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error))
    }
  }

}
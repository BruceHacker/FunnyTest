package com.yuanm.funneytest.imagevideo

import android.view.Surface

class GLEncodeCore(private val width: Int, private val height: Int) {

  private val eglEnv by lazy {
    EglEnv(width, height)
  }

  private val encodeProgram by lazy {
    EncodeProgram(width, height)
  }

  fun buildEGLSuraface(surface: Surface) {
    eglEnv.setUpEnv().buildWindowsSurface(surface)
    encodeProgram.build()
  }

}
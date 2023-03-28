package com.yuanm.funneytest.imagevideo

import android.graphics.Bitmap
import android.view.Surface

class GLEncodeCore(private val width: Int, private val height: Int) {

  private val eglEnv by lazy {
    EglEnv(width, height)
  }

  private val encodeProgram by lazy {
    EncodeProgram(width, height)
  }

  fun buildEGLSurface(surface: Surface) {
    // 构建EGL环境
    eglEnv.setUpEglEnv(surface)
    encodeProgram.build()
  }

  /**
   * @param bitmap raw bitmap to texture
   * @presentTime 纳秒，当前帧时间
   */
  fun drainFrame(bitmap: Bitmap, presentTime: Long) {
    encodeProgram.renderBitmap(bitmap)
    // 给渲染的这一帧设置一个时间戳
    eglEnv.setPresentationTime(presentTime)
    eglEnv.swapBuffers()
  }

  fun release() {
    eglEnv.release()
    encodeProgram.release()
  }


}
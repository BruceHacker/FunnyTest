package com.yuanm.funneytest

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.yuanm.funneytest.utils.AppUtil

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initApp()
  }

  private fun initApp() {
    AppUtil.app(this)
    Fresco.initialize(this)
  }

}
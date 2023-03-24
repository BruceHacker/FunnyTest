package com.yuanm.funneytest

import android.app.Application
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.yuanm.common.utils.AppUtil

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initApp()
  }

  private fun initApp() {
    Log.d("wymt", "application初始化设置")
    AppUtil.app(this)
    Fresco.initialize(this)
  }

}
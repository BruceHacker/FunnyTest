package com.yuanm.funneytest

import android.app.Application
import com.yuanm.funneytest.utils.AppUtil

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    AppUtil.app(this)
  }

}
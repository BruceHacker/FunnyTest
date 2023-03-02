package com.yuanm.funneytest.utils

import android.app.Application
import java.lang.Exception

object AppUtil {

  private var application: Application? = null

  fun app(application: Application) {
    this.application = application
  }

  fun app(): Application {
    return application ?: throw RuntimeException("需要在Application初始化时设置application")
  }

}
package com.yuanm.common.utils

import android.app.Application

object AppUtil {

  private var application: Application? = null

  fun app(application: Application) {
    this.application = application
  }

  @JvmStatic
  fun app(): Application {
    return application ?: throw RuntimeException("需要在Application初始化时设置application")
  }

}
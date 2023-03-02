package com.yuanm.funneytest.utils

import android.app.Application

object AppUtil {

  private var sharedApplication: Application? = null

  fun app(application: Application) {
    sharedApplication = application
  }

//  fun getContext(): Context {
//    if (sharedApplication == null) {
//
//    }
//    return sharedApplication
//  }

}
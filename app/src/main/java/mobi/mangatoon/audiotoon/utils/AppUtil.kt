package mobi.mangatoon.audiotoon.utils

import android.app.Application
import android.content.Context

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
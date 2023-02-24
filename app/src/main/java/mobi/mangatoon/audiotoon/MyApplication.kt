package mobi.mangatoon.audiotoon

import android.app.Application
import mobi.mangatoon.audiotoon.utils.AppUtil

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    AppUtil.app(this)
  }

}
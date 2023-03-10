package com.yuanm.funneytest.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager

object DeviceUtil {

  // 根据手机分辨率从dp转换成px（像素）
  @JvmStatic
  fun dip2px(dpValue: Int): Int {
    val scale = AppUtil.app().resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt() // 四舍五入取整
  }

  // 获取当前窗口的宽度
  @JvmStatic
  fun getDisplayWidth(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return windowManager.currentWindowMetrics.bounds.width()
    } else {
      val point = Point()
      windowManager.defaultDisplay.getSize(point)
      return point.x
    }
  }

}
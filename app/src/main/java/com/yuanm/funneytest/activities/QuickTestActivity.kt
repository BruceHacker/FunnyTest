package com.yuanm.funneytest.activities

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.view.SimpleDraweeView
import com.yuanm.common.utils.PermissionUtil
import com.yuanm.funneytest.R

class QuickTestActivity : AppCompatActivity() {

  private val imageView: SimpleDraweeView by lazy { findViewById(R.id.imageView) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_quick_test)
    imageView.setImageURI("http://test.mangatoon.mobi/cartoon-posters/5417029f18.webp-posterend4")
    initClickListener()
  }

  private fun initClickListener() {
    findViewById<View>(R.id.requestPermission).setOnClickListener {
      PermissionUtil.permissionsRequestEach(this, arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.SEND_SMS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
      )) {
        when {
          it.granted -> Log.d("yuanm", "你同意了权限：" + it.name)
          it.shouldShowRequestPermissionRationale -> Log.d("yuanm", "你拒绝了权限：" + it.name)
          else -> Log.d("yuanm", "你永远拒绝了权限：" + it.name)
        }
      }
    }
  }


}
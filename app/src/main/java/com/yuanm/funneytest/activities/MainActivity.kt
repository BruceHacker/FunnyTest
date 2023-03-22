package com.yuanm.funneytest.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yuanm.funneytest.R
import com.yuanm.funneytest.historyday.HistoryMainActivity
import com.yuanm.funneytest.imagevideo.ImageVideoActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

  private val historyDay: View by lazy { findViewById(R.id.historyDay) }

  private val twoList: View by lazy { findViewById(R.id.listBtn) }

  private val quickTest: View by lazy { findViewById(R.id.quickTest) }

  private val imageVideo: View by lazy { findViewById(R.id.imageVideo) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initClickListener()
  }

  private fun initClickListener() {
    historyDay.setOnClickListener(this)
    twoList.setOnClickListener(this)
    quickTest.setOnClickListener(this)
    imageVideo.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    val intent = Intent()
    when (v?.id) {
      R.id.historyDay -> {
        intent.setClass(this, HistoryMainActivity::class.java)
        startActivity(intent)
      }

      R.id.quickTest -> {
        intent.setClass(this, QuickTestActivity::class.java)
        startActivity(intent)
      }

      R.id.imageVideo -> {
        // todo： 临时处理了下，只适配了安卓10及以下，未适配高版本，安卓11与13适配方式不同
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
            .permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
          intent.setClass(this, ImageVideoActivity::class.java)
          startActivity(intent)
        }
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      1 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          val intent = Intent()
          intent.setClass(this, ImageVideoActivity::class.java)
          startActivity(intent)
        }
      }
    }
  }
}
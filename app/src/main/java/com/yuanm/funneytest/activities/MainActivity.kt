package com.yuanm.funneytest.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yuanm.common.utils.PermissionUtil
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
        PermissionUtil.permissionsRequest(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) {
          if (it) {
            intent.setClass(this, ImageVideoActivity::class.java)
            startActivity(intent)
          } else {
            Toast.makeText(this, "请同意权限申请", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

}
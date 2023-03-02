package com.yuanm.funneytest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.yuanm.funneytest.R
import com.yuanm.funneytest.utils.AppUtil

class MainActivity : AppCompatActivity(), View.OnClickListener {
  private val toHistory: View get() = findViewById(R.id.historyDay)
  private val imageView: SimpleDraweeView get() = findViewById(R.id.imageView)
  private val goToTwoList: View get() = findViewById(R.id.listBtn)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    toHistory.setOnClickListener(this)
    goToTwoList.setOnClickListener(this)
    imageView.setImageURI("http://test.mangatoon.mobi/cartoon-posters/5417029f18.webp-posterend4")
  }

  override fun onClick(v: View?) {
    val intent = Intent()
    when (v?.id) {
      R.id.historyDay -> {
        intent.setClass(this, HistoryMainActivity::class.java)
        startActivity(intent)
      }
    }
  }
}
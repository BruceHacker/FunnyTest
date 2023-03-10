package com.yuanm.funneytest.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.facebook.drawee.view.SimpleDraweeView
import com.yuanm.funneytest.R

class QuickTestActivity : AppCompatActivity() {

  private val imageView: SimpleDraweeView by lazy { findViewById(R.id.imageView) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_quick_test)
    imageView.setImageURI("http://test.mangatoon.mobi/cartoon-posters/5417029f18.webp-posterend4")
    findViewById<View>(R.id.tv2).setOnClickListener { v ->
      v.isVisible = false
    }
  }
}
package mobi.mangatoon.audiotoon.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import mobi.mangatoon.audiotoon.R

class HistoryMainActivity : AppCompatActivity() {
  private val listView: ListView get() = findViewById(R.id.main_lv)
  private val imageBtn: View get() = findViewById(R.id.main_imgbtn)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history_main)
    imageBtn.setOnClickListener{

    }
  }
}
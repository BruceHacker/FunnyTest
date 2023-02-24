package mobi.mangatoon.audiotoon.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import mobi.mangatoon.audiotoon.R

class UserReplacementSpan(val mText: String, val context: Context) : ReplacementSpan() {
  override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
    return paint.measureText(mText).toInt() + 50
  }

  override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
//    canvas.drawText("")
  }


  /**
   * 小说、对话小说段评被加精后段落末尾显示火苗图案，要能插入TextView的text末尾，而不是作为一个独立的view
   * 期初设想是纯用path去画，费事费力不讨好；后来发现可以把图片资源导入然后直接drawBitmap
   * 先画火苗，再画文字，要确保文字在火苗的内焰中间
   */
  private fun drawHotFire(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
    // 1.23f是火焰图片资源高宽比
    val fireWidth = (bottom - top) / 1.23f
    val rectF = RectF(x + 6, top.toFloat(), x + 6 + fireWidth, bottom.toFloat())
    val hotFire = BitmapFactory.decodeResource(context.resources, R.drawable.hot_fire)
    canvas.drawBitmap(hotFire, null, rectF, paint)
    // 画文字
    paint.color = context.resources.getColor(R.color.white)
    paint.textSize = 10f
    paint.textAlign = Paint.Align.CENTER




  }


}
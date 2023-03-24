package com.yuanm.funneytest.imagevideo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yuanm.funneytest.R
import java.io.File

/**
 * 在代码中，MediaCodeC只负责数据的传输，而生成MP4文件主要靠的类是MediaMuxer。整体上，项目涉及到的主要API有：
 * OpenGL，负责将图片绘制到Surface
 * MediaCodeC，图片编码为帧数据
 * MediaMuxer，帧数据编码为Mp4文件
 * 工作流程中所有环节都必须处在同一线程
 *
 * 流程：
 * 1.准备好需要用来制作视频的所有图片
 * 2.配置MediaCodeC（设置MIME类型、视频宽度和高度、比特率、帧速率等参数）
 * 3.通过 MediaCodec.createInputSurface()获取InputSurface对象
 * 4.配置MediaMuxer、EGL环境初始化并绑定MediaCodec的InputSurface、GPU程序初始化
 * 5.将图片数据加载到内存中，例如使用 Bitmap 或者其他图像处理库。
 * 6.GPU程序将Bitmap绘制到纹理上
 * 7.EGL环境调用swapBuffers提交数据到MediaCodec
 * 8.MediaCodec拿到可用输出，使用MediaMuxer写入视频文件
 * 9.重复5-8
 * 10.释放资源、关闭MediaCodec
 *
 * 建议：在处理大量图片时，应该使用异步方式处理，避免阻塞主线程。
 *
 * 参考博客：
 * 1.https://ailo.fun/2019/04/01/2019-04-01-MediaCodeC-encoder1/
 * 2.https://www.jianshu.com/p/54a702be01e1
 *
 */
class ImageVideoActivity : AppCompatActivity() {

  private val TAG = "Video_ImageVideoAct"

  // 测试文件夹，里面需要放置好图片
  private val encodePicDir =
    TextUtils.concat(Environment.getExternalStorageDirectory().path, "/Download/decode").toString()

  private val thread by lazy {
    val handlerThread = HandlerThread("encodeFrame")
    handlerThread.start()
    handlerThread
  }

  private val handler by lazy {
    Handler(thread.looper)
  }

  private val createBtn: View get() = findViewById(R.id.createBtn)
  private val playBtn: View get() = findViewById(R.id.playBtn)


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_image_video)
    initView()
    initClickListener()
  }

  private fun initView() {
    createBtn.isEnabled = true
    playBtn.isEnabled = false
  }

  private fun initClickListener() {
    createBtn.setOnClickListener {
      createVideo()
      it.isEnabled = false
    }
    playBtn.setOnClickListener {
      // todo: 播放生成的视频
    }
  }

  private fun createVideo() {
    handler.post {
      Log.d(TAG, "thread ${Thread.currentThread().name} ")
      val videoEncode = VideoEncoder(1080, 1920, 1800000, 24)
      videoEncode.start(
        Environment.getExternalStorageDirectory().path + "/Download/yuanm${System.currentTimeMillis()}.mp4")
      val file = File(encodePicDir)
      file.listFiles()?.forEachIndexed { index, file ->
        BitmapFactory.decodeFile(file.path)?.let {
          videoEncode.drainFrame(it, index)
        }
      }
      videoEncode.drainEnd()
      runOnUiThread {
        createBtn.isEnabled = true
        playBtn.isEnabled = true
        Toast.makeText(this, "视频已生成", Toast.LENGTH_SHORT).show()
      }
    }
  }

}
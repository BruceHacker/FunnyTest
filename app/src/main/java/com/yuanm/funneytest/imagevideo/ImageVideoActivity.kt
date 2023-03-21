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
 * 1.配置MediaCodeC、配置MediaMuxer、EGL环境初始化并绑定MediaCodec的InputSurface、GPU程序初始化
 * 2.拿到图片File的Bitmap
 * 3.GPU程序将Bitmap绘制到纹理上
 * 4.EGL环境调用swapBuffers提交数据到MediaCodec
 * 5.MediaCodec拿到可用输出，使用MediaMuxer写入视频文件
 * 重复2-5
 *
 * 参考博客：
 * 1.https://www.jianshu.com/p/54a702be01e1 or https://cloud.tencent.com/developer/article/1578977
 * 2.https://blog.csdn.net/JadynAi/article/details/89847026 or https://cloud.tencent.com/developer/article/1543042
 *
 *
 * ChatGPT的建议步骤：
 * 1.准备好需要用来制作视频的所有图片。
 * 2.将图片数据加载到内存中，例如使用 Bitmap 或者其他图像处理库。
 * 3.实例化一个 MediaCodec 对象并进行配置，设置 MIME 类型、视频宽度和高度、比特率等参数。
 * 4.创建一个 SurfaceView 用于渲染视频帧。
 * 5.通过 MediaCodec.createInputSurface() 获取输入 Surface 对象，并将其传递给渲染器。
 * 6.将每个图片数据编码成视频帧并通过输入 Surface 发送到 MediaCodec 中。
 * 7.获取 MediaCodec 输出的视频帧数据，并将其传递给渲染器。
 * 8.使用渲染器对视频帧进行渲染，并将结果展示在 SurfaceView 中。
 * 9.循环执行步骤 6 - 8 直到所有图片都被编码以形成完整的视频流。
 * 10.释放资源并关闭 MediaCodec 对象。
 * 建议：
 * 1.在处理大量图片时，应该使用异步方式处理，避免阻塞主线程。
 * 2.考虑使用硬件加速技术，例如 OpenGL ES，以提高性能和效率。
 * 3.了解一些关于视频编解码和容器格式的知识，有助于更好地理解和调试代码。
 * 4.在开发过程中，可以使用 Android Studio 提供的性能分析工具进行调试和优化。
 *
 */
class ImageVideoActivity : AppCompatActivity() {

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
    playBtn.isEnabled = false
    initClickListener()
  }

  private fun initClickListener() {
    createBtn.setOnClickListener {
      createVideo()
    }
    playBtn.setOnClickListener {
      // todo: 播放生成的视频
    }
  }

  private fun createVideo() {
    handler.post {
      Log.d("wymt", "thread ${Thread.currentThread().name} ")
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
      playBtn.isEnabled = true
      Toast.makeText(this, "视频已生成", Toast.LENGTH_SHORT).show()
    }
  }

}
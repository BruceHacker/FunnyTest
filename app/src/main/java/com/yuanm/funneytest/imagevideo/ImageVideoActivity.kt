package com.yuanm.funneytest.imagevideo

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yuanm.funneytest.R

/**
 * 使用MediaCodeC将图片集编码为视频文件
 * 在代码中，MediaCodeC只负责数据的传输，而生成MP4文件主要靠的类是MediaMuxer。整体上，项目涉及到的主要API有：
 * MediaCodeC，图片编码为帧数据
 * MediaMuxer，帧数据编码为Mp4文件
 * OpenGL，负责将图片绘制到Surface
 * 工作流程中所有环节都必须处在同一线程
 *
 * 参考博客：
 * 1.https://www.jianshu.com/p/54a702be01e1 or https://cloud.tencent.com/developer/article/1578977
 * 2.https://blog.csdn.net/JadynAi/article/details/89847026
 */
class ImageVideoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_image_video)
    getMediaCodeList()
  }

  // 获取当前设备可渲染的颜色空间模式
  private fun getMediaCodeList(): IntArray? {
    val mediaCodeList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    val supportedCodeInfos = mediaCodeList.codecInfos
    var codeInfo: MediaCodecInfo? = null
    for (info in supportedCodeInfos) {
      if (!info.isEncoder) {
        continue
      }
      Log.d("wymt", "硬解： " + info.name)
      val supportedTypes = info.supportedTypes
      var found = false
      for (type in supportedTypes) {
        if (type.equals("video/avc")) {
          found = true
          break
        }
      }
      if (!found) {
        continue
      }
      codeInfo = info
      break
    }
    return codeInfo?.let {
      Log.d("wymt", "found " + it.name + " supporting" + " video/avc")
      val capabilities = it.getCapabilitiesForType("video/avc")
      capabilities.colorFormats
    }
  }


//  private fun dd() {
//    val runnable = Runnable {
//      val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
//      // MediaFormat配置颜色格式、比特率、帧率、关键帧
//      val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
//        setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)
//        setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
//        setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
//        setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)
//      }
//
//      mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//      val inputSurface = mediaCodec.createInputSurface()
//      mediaCodec.start()
//    }
//    Thread(runnable).start()
//  }
}
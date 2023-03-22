package com.yuanm.funneytest.imagevideo

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import java.io.File
import java.io.IOException

class VideoEncoder(private val width: Int, private val height: Int, private val bitRate: Int, var frameRate: Int = 24,
  frameInterval: Int = 5) {

  private val TAG = "Video_VideoEncoder"

  private val codec: MediaCodec

  private val mediaFormat by lazy {
    MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
      // MediaFormat配置颜色格式、比特率、帧率、关键帧
      setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
      setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
      setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
      setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, frameInterval)
    }
  }

  private val encodeCore by lazy {
    GLEncodeCore(width, height)
  }

  private val bufferInfo by lazy {
    MediaCodec.BufferInfo()
  }

  private lateinit var inputSurface: Surface

  private var mediaMuxer: MediaMuxer? = null

  private var muxerStarted: Boolean = false
  private var trackIndex: Int = 0


  init {
    try {
      codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    } catch (e: IOException) {
      throw RuntimeException("codec init failed $e")
    }
  }


  fun start(outputPath: String) {
    val file = File(outputPath)
    if (file.exists()) {
      file.delete()
    }
    codec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    inputSurface = codec.createInputSurface()
    try {
      mediaMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    } catch (e: IOException) {
      throw RuntimeException("create media muxer failed $e")
    }
    codec.start()
    encodeCore.buildEGLSurface(inputSurface)
  }


  fun drainFrame(bitmap: Bitmap, index: Int) {
    // 每张图片持续5帧时间
    val presentTime = index * 1000000000L / frameRate * 10
    encodeCore.drainFrame(bitmap, presentTime)
    drainCoder(false)
  }

  fun drainEnd() {
    drainCoder(true)
    encodeCore.release()
    codec.stop()
    mediaMuxer?.stop()
    mediaMuxer?.release()
    mediaMuxer = null
  }

  private fun drainCoder(endOfSteams: Boolean) {
    if (endOfSteams) {
      codec.signalEndOfInputStream()
    }
    val defTimeOut = 1000L
    codec.handleOutputBuffer(bufferInfo, defTimeOut, {
      if (muxerStarted) {
        throw RuntimeException("already muxer started")
      }
      Log.d(TAG, "format changed ${codec.outputFormat}")
      trackIndex = mediaMuxer!!.addTrack(codec.outputFormat)
      mediaMuxer!!.start()
      muxerStarted = true
    }, {
      val encodeData = codec.getOutputBuffer(it)
      Log.d(TAG, "buffer info flag ${bufferInfo.flags}")
      if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
        bufferInfo.size = 0
      }
      if (bufferInfo.size != 0) {
        if (!muxerStarted) {
          throw RuntimeException("muxer hasn't started")
        }
        Log.d(TAG, "buffer info offset ${bufferInfo.offset} time is ${bufferInfo.presentationTimeUs}")
        if (encodeData != null) {
          encodeData.position(bufferInfo.offset)
          encodeData.limit(bufferInfo.offset + bufferInfo.size)
          mediaMuxer!!.writeSampleData(trackIndex, encodeData, bufferInfo)
        }
        codec.releaseOutputBuffer(it, false)
      }
    }, !endOfSteams)
  }


  // 获取当前设备可渲染的颜色空间模式
//  private fun getMediaCodeList(): IntArray? {
//    val mediaCodeList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
//    val supportedCodeInfos = mediaCodeList.codecInfos
//    var codeInfo: MediaCodecInfo? = null
//    for (info in supportedCodeInfos) {
//      if (!info.isEncoder) {
//        continue
//      }
//      Log.d("wymt", "硬解： " + info.name)
//      val supportedTypes = info.supportedTypes
//      var found = false
//      for (type in supportedTypes) {
//        if (type.equals("video/avc")) {
//          found = true
//          break
//        }
//      }
//      if (!found) {
//        continue
//      }
//      codeInfo = info
//      break
//    }
//    return codeInfo?.let {
//      Log.d("wymt", "found " + it.name + " supporting" + " video/avc")
//      val capabilities = it.getCapabilitiesForType("video/avc")
//      capabilities.colorFormats
//    }
//  }
//
//  // 得到颜色空间模式后，判断选择其中一种来对图片进行编码
//  private fun getColorFormat(): Int {
//    var colorFormat = 0
//    val formats = getMediaCodeList()
//    formats?.let {
//      lab@ for (format in it) {
//        when (format) {
//          MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible -> {
//            colorFormat = format
//            break@lab
//          }
//        }
//      }
//    }
//    if (colorFormat <= 0) {
//      colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
//    }
//    return colorFormat
//  }
//
//  // 配置MediaCodec
//  private fun dd() {
//    val runnable = Runnable {
//      val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
//      val colorFormat = getColorFormat()
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
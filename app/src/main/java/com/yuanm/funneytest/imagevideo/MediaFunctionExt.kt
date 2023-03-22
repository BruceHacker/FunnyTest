package com.yuanm.funneytest.imagevideo

import android.media.MediaCodec
import android.media.MediaCodec.BufferInfo
import android.util.Log

/**
 * 视频项目中的各扩展类
 */


/**
 * @param needEnd when bufferId is INFO_TRY_AGAIN_LATER，need to break loop
 */
fun MediaCodec.handleOutputBuffer(bufferInfo: BufferInfo, defTimeOut: Long, formatChanged: () -> Unit = {},
  render: (bufferId: Int) -> Unit, needEnd: Boolean = true) {
  loopOut@ while (true) {
    // 获取可用的输出缓存队列
    val outputBufferId = dequeueOutputBuffer(bufferInfo, defTimeOut)
    Log.d("handleOutputBuffer", "output buffer id: $outputBufferId")
    if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
      if (needEnd) {
        break@loopOut
      }
    } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
      formatChanged.invoke()
    } else if (outputBufferId >= 0) {
      render.invoke(outputBufferId)
      if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
        break@loopOut
      }
    }
  }
}
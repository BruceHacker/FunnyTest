package com.yuanm.funneytest.utils

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ImageDecodeOptions
import com.facebook.imagepipeline.filter.RenderScriptBlurFilter
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.postprocessors.BlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.IOException

class ImageViewUtil {


//  private suspend fun renderShareView(item: ContentAchievementItem): View {
//
//    return suspendCancellableCoroutine { continuation ->
//      var imageLoadCount = 2
//      val shareView = LayoutInflater.from(context).inflate(R.layout.layout_content_achievement_milestone, null)
//      shareView.findViewById<TextView>(R.id.achievementTv).text = item.achievement
//      shareView.findViewById<TextView>(R.id.title).text = item.contentTitle
//      shareView.findViewById<TextView>(R.id.subtitle).text = item.message
//      val coverImage = shareView.findViewById<ImageView>(R.id.coverImage)
//      val bigImg = shareView.findViewById<ImageView>(R.id.bigImg)
//      shareView.measure(
//        View.MeasureSpec.makeMeasureSpec(itemView.width, View.MeasureSpec.EXACTLY),
//        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//      shareView.layout(0, 0, shareView.measuredWidth, shareView.measuredHeight)
//
//      val callback: (Boolean) -> Unit = {
//        if (continuation.isActive) {
//          if (it.not()) {
//            continuation.resumeWithException(IOException("load image failed "))
//          } else {
//            imageLoadCount--
//            if (imageLoadCount <= 0) {
//              continuation.resume(shareView)
//            }
//          }
//        }
//      }
//      val imageUri = Uri.parse(item.imageUrl)
//      rendImageView(imageUri, coverImage, false, callback)
//      rendImageView(imageUri, bigImg, true, callback)
//      shareView
//    }
//  }
//
//  private fun rendImageView(imageUri: Uri?, draweeView: ImageView, isBlur: Boolean, callback: (Boolean) -> Unit) {
//    val imageReq = if (isBlur) {
//      ImageRequestBuilder.newBuilderWithSource(imageUri)
//        .setPostprocessor(BlurPostProcessor(RenderScriptBlurFilter.BLUR_MAX_RADIUS, draweeView.context, 10))
//        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build()
//    } else {
//      ImageRequestBuilder.newBuilderWithSource(imageUri)
//        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build()
//    }
//
//    Fresco.getImagePipeline().fetchDecodedImage(imageReq, draweeView.context)
//      .subscribe(object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
//        override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
//          (dataSource.result?.get() as? CloseableBitmap)?.also {
//            draweeView.setImageBitmap(it.underlyingBitmap)
//            callback.invoke(true)
//          } ?: kotlin.run {
//            callback.invoke(false)
//          }
//        }
//
//        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
//          callback.invoke(false)
//        }
//      }, CallerThreadExecutor.getInstance())
//  }

}
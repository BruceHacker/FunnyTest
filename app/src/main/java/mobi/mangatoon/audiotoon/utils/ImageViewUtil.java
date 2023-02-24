package mobi.mangatoon.audiotoon.utils;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.filter.RenderScriptBlurFilter;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.postprocessors.BlurPostProcessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import mobi.mangatoon.audiotoon.R;

public class ImageViewUtil {

  private View renderShareView(ContentDetailResultDataModel model) {
    View shareView = LayoutInflater.from(this).inflate(R.layout.layout_content_achievement_milestone, null);
    ((TextView) shareView.findViewById(R.id.achievementTv)).setText(model.popularMileStone.value);
    ((TextView) shareView.findViewById(R.id.title)).setText(model.title);
    ((TextView) shareView.findViewById(R.id.subtitle)).setText(model.popularMileStone.message);
    ImageView coverImage = (ImageView) shareView.findViewById(R.id.coverImage);
    ImageView bigImg = (ImageView) shareView.findViewById(R.id.bigImg);
    shareView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());

    rendImageView(Uri.parse(model.imageUrl), coverImage, false);
    rendImageView(Uri.parse(model.imageUrl), bigImg, true);
    return shareView;
  }

  private void rendImageView(Uri imageUri, ImageView imageView, boolean isBlur) {
    ImageRequest request = null;
    if (isBlur) {
      request = ImageRequestBuilder.newBuilderWithSource(imageUri).setPostprocessor(new BlurPostProcessor(RenderScriptBlurFilter.BLUR_MAX_RADIUS, this, 10))
        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build();
    } else {
      request = ImageRequestBuilder.newBuilderWithSource(imageUri)
        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build();
    }
    Fresco.getImagePipeline().fetchDecodedImage(request, null).subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
      @Override
      protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
        if (!dataSource.isFinished() || !ContextUtil.isActive(getActivity())) {
          return;
        }
        CloseableBitmap bitmap = (CloseableBitmap) dataSource.getResult().get();
        imageView.setImageBitmap(bitmap.getUnderlyingBitmap());
      }

      @Override
      protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

      }
    }, UiThreadImmediateExecutorService.getInstance());
  }
}

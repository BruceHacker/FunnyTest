package com.yuanm.common.utils;

public class ImageViewUtils {

//  private View renderShareView(ContentDetailResultDataModel model) {
//    View shareView = LayoutInflater.from(this).inflate(R.layout.layout_content_achievement_milestone, null);
//    ((TextView) shareView.findViewById(R.id.achievementTv)).setText(model.popularMileStone.value);
//    ((TextView) shareView.findViewById(R.id.title)).setText(model.title);
//    ((TextView) shareView.findViewById(R.id.subtitle)).setText(model.popularMileStone.message);
//    ImageView coverImage = (ImageView) shareView.findViewById(R.id.coverImage);
//    ImageView bigImg = (ImageView) shareView.findViewById(R.id.bigImg);
//    shareView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//    shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());
//
//    rendImageView(Uri.parse(model.imageUrl), coverImage, false);
//    rendImageView(Uri.parse(model.imageUrl), bigImg, true);
//    return shareView;
//  }

//  private void rendImageView(Uri imageUri, ImageView imageView, boolean isBlur) {
//    ImageRequest request = null;
//    if (isBlur) {
//      request = ImageRequestBuilder.newBuilderWithSource(imageUri).setPostprocessor(new BlurPostProcessor(RenderScriptBlurFilter.BLUR_MAX_RADIUS, this, 10))
//        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build();
//    } else {
//      request = ImageRequestBuilder.newBuilderWithSource(imageUri)
//        .setImageDecodeOptions(ImageDecodeOptions.newBuilder().setForceStaticImage(true).build()).build();
//    }
//    Fresco.getImagePipeline().fetchDecodedImage(request, null).subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
//      @Override
//      protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//        if (!dataSource.isFinished() || !ContextUtil.isActive(getActivity())) {
//          return;
//        }
//        CloseableBitmap bitmap = (CloseableBitmap) dataSource.getResult().get();
//        imageView.setImageBitmap(bitmap.getUnderlyingBitmap());
//      }
//
//      @Override
//      protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//
//      }
//    }, UiThreadImmediateExecutorService.getInstance());
//  }
}

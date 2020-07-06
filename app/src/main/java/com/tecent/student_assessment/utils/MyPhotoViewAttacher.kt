package com.tecent.student_assessment.utils

import android.widget.ImageView
import uk.co.senab.photoview.PhotoViewAttacher

class MyPhotoViewAttacher : PhotoViewAttacher {
  constructor(imageView: ImageView?) : super(imageView)
  constructor(
    imageView: ImageView?,
    zoomable: Boolean
  ) : super(imageView, zoomable) {
  }

  override fun onGlobalLayout() {
    try {
      super.onGlobalLayout()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
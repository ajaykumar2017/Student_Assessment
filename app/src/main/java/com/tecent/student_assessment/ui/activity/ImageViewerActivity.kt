package com.tecent.student_assessment.ui.activity

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.tecent.student_assessment.R.layout
import kotlinx.android.synthetic.main.activity_image_viewer.imageView
import uk.co.senab.photoview.PhotoViewAttacher

class ImageViewerActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        layout.activity_image_viewer
    )
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //Prevent landscape mode

    changeStatusBarColor()
    when (intent.getStringExtra("intentType")) {
      "byteArray" -> {
        val byteArray = intent.getByteArrayExtra("imageByteArray")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imageView.setImageBitmap(bmp)
      }
    }

    val pAttacher = PhotoViewAttacher(imageView)
    pAttacher.update()
  }

  private fun changeStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val window = window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.BLACK
    }
  }

}

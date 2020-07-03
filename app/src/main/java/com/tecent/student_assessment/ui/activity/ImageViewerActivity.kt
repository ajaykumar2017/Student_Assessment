package com.tecent.student_assessment.ui.activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_image_viewer.*
import uk.co.senab.photoview.PhotoViewAttacher
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import com.tecent.student_assessment.R.layout

class ImageViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_image_viewer
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //Prevent landscape mode

        changeStatusBarColor()
        val intentType=intent.getStringExtra("intentType")
        when(intentType){
            "byteArray"->{
                val byteArray=intent.getByteArrayExtra("imageByteArray")
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

package com.tecent.student_assessment.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.tecent.student_assessment.R.string
import com.tecent.student_assessment.ui.activity.ImageViewerActivity
import java.io.ByteArrayOutputStream

class ExtraFunctions(val context: Context) {

  fun copyTextToClipboard(
    textToBeCopied: String,
    charSequence: CharSequence
  ): Boolean {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText(
        textToBeCopied, charSequence
    )
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Text Copied to clipboard", Toast.LENGTH_SHORT)
        .show()
    return true
  }

  fun animateIntent(view: ImageView) {
    try {
      val intent = Intent(context, ImageViewerActivity::class.java)
      intent.putExtra("intentType", "byteArray")
      intent.putExtra(
          "imageByteArray",
          getFileDataFromDrawable(context, view.drawable)
      )
      val transitionName = context.getString(
          string.transition_string
      )
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
          context as Activity, view as View, transitionName
      )
      ActivityCompat.startActivity(context, intent, options.toBundle())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun getFileDataFromDrawable(
    context: Context,
    drawable: Drawable
  ): ByteArray {
    val bitmap = (drawable as BitmapDrawable).bitmap
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
  }
}
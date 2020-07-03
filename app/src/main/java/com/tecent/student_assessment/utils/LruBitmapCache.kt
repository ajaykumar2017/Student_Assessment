package com.tecent.student_assessment.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader.ImageCache

/**
 * Custom caching class volley image loader.
 *
 * Sketch Project Studio
 * Created by Angga on 22/04/2016 23.29.
 */
class LruBitmapCache(maxSize: Int) : LruCache<String?, Bitmap>(
    maxSize
), ImageCache {
  constructor(context: Context) : this(
      getCacheSize(context)
  ) {
  }

  override fun sizeOf(
    key: String?,
    value: Bitmap
  ): Int {
    return value.rowBytes * value.height
  }

  override fun getBitmap(url: String): Bitmap {
    return get(url)
  }

  override fun putBitmap(
    url: String,
    bitmap: Bitmap
  ) {
    put(url, bitmap)
  }

  companion object {
    /**
     * Returns a cache size equal to approximately three screens worth of images.
     *
     * @param context parent context
     * @return int size of cache
     */
    fun getCacheSize(context: Context): Int {
      val displayMetrics = context.resources
          .displayMetrics
      val screenWidth = displayMetrics.widthPixels
      val screenHeight = displayMetrics.heightPixels
      // 4 bytes per pixel
      val screenBytes = screenWidth * screenHeight * 4
      return screenBytes * 3
    }
  }
}
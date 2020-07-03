@file:Suppress("NAME_SHADOWING")

package com.tecent.student_assessment.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap.Config.RGB_565
import android.net.ConnectivityManager
import android.os.Environment
import android.widget.ImageView
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.ImageRequest
import com.tecent.student_assessment.ui.activity.StudentLoginActivity

object ExtraFunctions {
  @JvmField var serverurl = "https://www.sas.a3creators.co.in/project/"
  var rootdir = Environment.getExternalStorageDirectory()
      .toString() + "/Android/data/com.tecent.studentAssessment/"
  var sharedPreferencesId = "studentAssessment"
  var sharedPreferencesLikeId = "postLikes"
  var ROOTMAIN = Environment.getExternalStorageDirectory()
      .toString() + "/"

  fun getFullBranch(branch: String): String {
    return when (branch) {
      "cse" -> "Computer Science and Engineering"
      "ece" -> "Electronics and Communication Engineering"
      "me" -> "Mechanical Engineering"
      "ee" -> "Electrical Engineering"
      "ce" -> "Civil Engineering"
      else -> "Others"
    }
  }

  fun getFullUniversity(university: String): String {
    return when (university) {
      "KU" -> "Kolhan University"
      "JUT" -> "Jharkhand University of Technology"
      else -> "Others"
    }
  }

  fun getFullSemester(semester: String): String {
    return when (semester) {
      "1" -> "1st Semester"
      "2" -> "2nd Semester"
      "3" -> "3rd Semester"
      "4" -> "4th Semester"
      "5" -> "5th Semester"
      "6" -> "6th Semester"
      "7" -> "7th Semester"
      "8" -> "8th Semester"
      else -> "Others"
    }
  }

  fun getSmallUniversity(university: String): String {
    var universityValue = ""
    when (university) {
      "Jharkhand University of Technology" -> universityValue = "JUT"
      "Kolhan University" -> universityValue = "KU"
      "All" -> universityValue = "All"
      "Others" -> universityValue = "Others"
    }
    return universityValue
  }

  fun getSmallBranch(branch: String): String {
    var branchValue = ""
    when (branch) {
      "Computer Science and Engineering" -> branchValue = "cse"
      "Electronics and Communication Engineering" -> branchValue = "ece"
      "Mechanical Engineering" -> branchValue = "me"
      "Electrical Engineering" -> branchValue = "ee"
      "Civil Engineering" -> branchValue = "ce"
    }
    return branchValue
  }

  fun getSmallSemester(semester: String): String {
    var semesterValue = ""
    when (semester) {
      "1st Semester" -> semesterValue = "1"
      "2nd Semester" -> semesterValue = "2"
      "3rd Semester" -> semesterValue = "3"
      "4th Semester" -> semesterValue = "4"
      "5th Semester" -> semesterValue = "5"
      "6th Semester" -> semesterValue = "6"
      "7th Semester" -> semesterValue = "7"
      "8th Semester" -> semesterValue = "8"
    }
    return semesterValue
  }

  fun isValidEmailId(emailId: String): Boolean {
    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return emailPattern.matches(emailId)
  }

  fun isNetworkStatusAvailable(context: Context): Boolean {
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfos = connectivityManager.activeNetworkInfo
    if (netInfos != null) if (netInfos.isConnected) return true
    return false
  }

  fun createImageRequestFromUrl(
    url: String?,
    imageView: ImageView
  ): ImageRequest {
    return ImageRequest(url,
        Listener { response -> imageView.setImageBitmap(response) }, 0, 0, null,
        RGB_565,
        ErrorListener { })
  }

  fun isValidPostId(postId: String): Boolean {
    val pattern = Regex("[0-9]+")
    return pattern.matches(postId)
  }

  fun logOut(context: Activity) {
    context.getSharedPreferences(sharedPreferencesId, 0)
        .edit()
        .clear()
        .apply()
    context.getSharedPreferences(sharedPreferencesLikeId, 0)
        .edit()
        .clear()
        .apply()
    context.startActivity(Intent(context, StudentLoginActivity::class.java))
    context.finish()
  }
}
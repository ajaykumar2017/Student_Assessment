package com.tecent.student_assessment.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.utils.AppHelper
import com.tecent.student_assessment.R.array
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.string
import com.tecent.student_assessment.utils.VolleyMultipartRequest
import com.tecent.student_assessment.utils.VolleySingleton
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsAdapter
import com.tecent.student_assessment.ui.adapters.MyRecyclerPostDoubtsAdapter
import com.tecent.student_assessment.utils.ExtraFunctions
import com.tecent.student_assessment.utils.ExtraFunctions.getFullBranch
import com.tecent.student_assessment.utils.ExtraFunctions.getFullSemester
import com.tecent.student_assessment.utils.ExtraFunctions.getFullUniversity
import com.tecent.student_assessment.utils.ExtraFunctions.getSmallBranch
import com.tecent.student_assessment.utils.ExtraFunctions.getSmallSemester
import com.tecent.student_assessment.utils.ExtraFunctions.getSmallUniversity
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_profile.edit_branch
import kotlinx.android.synthetic.main.activity_profile.edit_college
import kotlinx.android.synthetic.main.activity_profile.edit_gender
import kotlinx.android.synthetic.main.activity_profile.edit_name
import kotlinx.android.synthetic.main.activity_profile.edit_semester
import kotlinx.android.synthetic.main.activity_profile.edit_university
import kotlinx.android.synthetic.main.activity_profile.iv_change_dp
import kotlinx.android.synthetic.main.activity_profile.iv_profile_image
import kotlinx.android.synthetic.main.activity_profile.progress_bar_profile
import kotlinx.android.synthetic.main.activity_profile.tv_active_since
import kotlinx.android.synthetic.main.activity_profile.tv_answers
import kotlinx.android.synthetic.main.activity_profile.tv_branch
import kotlinx.android.synthetic.main.activity_profile.tv_college
import kotlinx.android.synthetic.main.activity_profile.tv_doubts
import kotlinx.android.synthetic.main.activity_profile.tv_email
import kotlinx.android.synthetic.main.activity_profile.tv_gender
import kotlinx.android.synthetic.main.activity_profile.tv_my_posts
import kotlinx.android.synthetic.main.activity_profile.tv_my_posts_doubts
import kotlinx.android.synthetic.main.activity_profile.tv_name
import kotlinx.android.synthetic.main.activity_profile.tv_posts
import kotlinx.android.synthetic.main.activity_profile.tv_semester
import kotlinx.android.synthetic.main.activity_profile.tv_university
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.btn_submit
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.et_name
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.rad_female
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.rad_male
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.radioGroup_gender
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.select_subject
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.sp_branch
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.sp_college
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.sp_semester
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.sp_university
import kotlinx.android.synthetic.main.toolbar_main.toolbar_main
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.ArrayList
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProfileActivity : AppCompatActivity() {
  //posts
  internal lateinit var useridlist: ArrayList<String>
  internal lateinit var userdplist: ArrayList<String>
  internal lateinit var usernamelist: ArrayList<String>
  internal lateinit var userbranchlist: ArrayList<String>
  internal lateinit var posttimelist: ArrayList<String>
  internal lateinit var postidlist: ArrayList<String>
  internal lateinit var posttextlist: ArrayList<String>
  internal lateinit var postfilelist: ArrayList<String>
  internal lateinit var subjectlist: ArrayList<String>
  internal lateinit var likeslist: ArrayList<String>
  internal lateinit var commentslist: ArrayList<String>

  //doubts
  internal lateinit var useridpostlist: ArrayList<String>
  internal lateinit var usernamepostlist: ArrayList<String>
  internal lateinit var userdppostlist: ArrayList<String>
  internal lateinit var posttimepostlist: ArrayList<String>
  internal lateinit var userbranchpostlist: ArrayList<String>
  internal lateinit var postdoubtidpostlist: ArrayList<String>
  internal lateinit var posttextpostlist: ArrayList<String>
  internal lateinit var postimagepostlist: ArrayList<String>
  internal lateinit var postnoofanswerslist: ArrayList<String>

  lateinit var sharedPreferences: SharedPreferences
  lateinit var sharedPreferencesLike: SharedPreferences
  lateinit var recyclerViewProfile: RecyclerView
  lateinit var recyclerViewProfilePostsDoubts: RecyclerView
  lateinit var requestQueue: RequestQueue
  lateinit var ivChangeDp: ImageView
  lateinit var dialog: ACProgressFlower
  lateinit var userid: String
  lateinit var userdp: String
  lateinit var postByUserId: String
  private lateinit var postByUserName: String
  private lateinit var postByUserDp: String
  private lateinit var postByUserBranch: String

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_profile)
    setSupportActionBar(findViewById(id.toolbar_main))
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.setHomeAsUpIndicator(drawable.ic_001_back)
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
    val intent = intent
    postByUserId = ""
    postByUserName = ""
    postByUserBranch = ""
    postByUserDp = ""
    userid = ""
    ivChangeDp = findViewById(id.iv_change_dp)
    val profile: String = intent.getStringExtra("profile")
    recyclerViewProfile = findViewById(id.recyclerView_profile)
    recyclerViewProfile.setHasFixedSize(true)
    recyclerViewProfile.layoutManager =
      LinearLayoutManager(this)
    recyclerViewProfilePostsDoubts = findViewById(id.recyclerView_profile_doubts)
    recyclerViewProfilePostsDoubts.setHasFixedSize(true)
    recyclerViewProfilePostsDoubts.layoutManager =
      LinearLayoutManager(this)
    sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
    sharedPreferencesLike = this.getSharedPreferences("postLikes", Context.MODE_PRIVATE)
    userid = this.sharedPreferences.getString("userid", "")
    requestQueue = Volley.newRequestQueue(this)
    dialog = ACProgressFlower.Builder(this)
        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
        .themeColor(Color.WHITE)
        .fadeColor(Color.BLACK)
        .build()
    dialog.setCancelable(false)
    if (profile == "MyProfile") {
      supportActionBar!!.title = "My Profile"
      val email: String = sharedPreferences.getString("email", "")
      val name: String = sharedPreferences.getString("name", "")
      val gender: String = sharedPreferences.getString("gender", "")
      val branch: String = sharedPreferences.getString("branch", "")
      val semester: String = sharedPreferences.getString("semester", "")
      val college: String = sharedPreferences.getString("college", "")
      val university: String = sharedPreferences.getString("university", "")
      userdp = sharedPreferences.getString("userdp", "")
      val joindate: String = sharedPreferences.getString("joindate", "")
      val posts: String = sharedPreferences.getString("posts", "")
      val doubts: String = sharedPreferences.getString("doubts", "")
      val answers: String = sharedPreferences.getString("answers", "")
      requestQueue.add(
          ExtraFunctions.createImageRequestFromUrl(
              ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image
          )
      )
      tv_name.text = name
      tv_active_since.text = joindate
      tv_email.text = email
      tv_gender.text = gender
      tv_branch.text = getFullBranch(branch)
      tv_semester.text = getFullSemester(semester)
      tv_college.text = college
      tv_university.text = getFullUniversity(university)
      tv_posts.text = posts
      tv_doubts.text = doubts
      tv_answers.text = answers
      iv_profile_image.setOnClickListener {
        animateIntent(iv_profile_image)
      }
      if (ExtraFunctions.isNetworkStatusAvailable(this)) {
        volleyPostDataRequest(userid)
        volleyDoubtsPostsDataRequest(userid)
      } else {
        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT)
            .show()
      }
      val editProfileDialog = Dialog(this)
      editProfileDialog.setContentView(layout.custom_dialog_edit_profile)

      val spinnerBranch = editProfileDialog.findViewById<Spinner>(id.sp_branch)
      val valuesBranch = resources.getStringArray(array.branches)
      val adapterBranch = ArrayAdapter(this, layout.spinner_item, valuesBranch)
      spinnerBranch.adapter = adapterBranch

      val spinnerSemester = editProfileDialog.findViewById<Spinner>(id.sp_semester)
      val valuesSemester = resources.getStringArray(array.semester)
      val adapterSemester = ArrayAdapter(this, layout.spinner_item, valuesSemester)
      spinnerSemester.adapter = adapterSemester

      val spinnerCollege = editProfileDialog.findViewById<Spinner>(id.sp_college)
      val valuesCollege = resources.getStringArray(array.collegelist)
      val adapterCollege = ArrayAdapter(this, layout.spinner_item, valuesCollege)
      spinnerCollege.adapter = adapterCollege

      val spinnerUniversity = editProfileDialog.findViewById<Spinner>(id.sp_university)
      val valuesUniversity = resources.getStringArray(array.university)
      val adapterUniversity = ArrayAdapter(this, layout.spinner_item, valuesUniversity)
      spinnerUniversity.adapter = adapterUniversity

      editProfileDialog.et_name.visibility = View.GONE
      editProfileDialog.radioGroup_gender.visibility = View.GONE
      editProfileDialog.sp_branch.visibility = View.GONE
      editProfileDialog.sp_semester.visibility = View.GONE
      editProfileDialog.sp_college.visibility = View.GONE
      editProfileDialog.sp_university.visibility = View.GONE

      //edit name
      val sharedPreferencesEdit = sharedPreferences.edit()
      edit_name.setOnClickListener {
        editProfileDialog.select_subject.text = "Enter New Name"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.VISIBLE
        editProfileDialog.radioGroup_gender.visibility = View.GONE
        editProfileDialog.sp_branch.visibility = View.GONE
        editProfileDialog.sp_semester.visibility = View.GONE
        editProfileDialog.sp_college.visibility = View.GONE
        editProfileDialog.sp_university.visibility = View.GONE
        editProfileDialog.et_name.setText(
            this.sharedPreferences.getString("name", ""), TextView.BufferType.EDITABLE
        )
        editProfileDialog.btn_submit.setOnClickListener {
          if (editProfileDialog.et_name.text.toString()
                  .trim() == ""
          ) {
            Toast.makeText(this, "Please write your new Name", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "Name Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString(
                          "name", editProfileDialog.et_name.text.toString()
                          .trim()
                      )
                      sharedPreferencesEdit.putBoolean("dataChange", true)
                      sharedPreferencesEdit.apply()
                      tv_name.text = editProfileDialog.et_name.text.toString()
                          .trim()
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["name"] = editProfileDialog.et_name.text.toString()
                        .trim()
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }


      edit_gender.setOnClickListener {
        editProfileDialog.select_subject.text = "Enter New Gender"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.GONE
        editProfileDialog.radioGroup_gender.visibility = View.VISIBLE
        editProfileDialog.sp_branch.visibility = View.GONE
        editProfileDialog.sp_semester.visibility = View.GONE
        editProfileDialog.sp_college.visibility = View.GONE
        editProfileDialog.sp_university.visibility = View.GONE
        editProfileDialog.btn_submit.setOnClickListener {
          if (editProfileDialog.radioGroup_gender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "select your gender", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val genderValue: String = when {
                editProfileDialog.rad_male.isChecked -> {
                  "Male"
                }
                editProfileDialog.rad_female.isChecked -> {
                  "Female"
                }
                else -> {
                  "Others"
                }
              }
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "Gender Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString("gender", genderValue)
                      sharedPreferencesEdit.apply()
                      tv_gender.text = genderValue
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "volley error", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["gender"] = genderValue
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }
      edit_branch.setOnClickListener {
        editProfileDialog.select_subject.text = "Select New Branch"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.GONE
        editProfileDialog.radioGroup_gender.visibility = View.GONE
        editProfileDialog.sp_branch.visibility = View.VISIBLE
        editProfileDialog.sp_semester.visibility = View.GONE
        editProfileDialog.sp_college.visibility = View.GONE
        editProfileDialog.sp_university.visibility = View.GONE
        editProfileDialog.btn_submit.setOnClickListener {
          if (spinnerBranch.selectedItem.toString()
                  .trim { it <= ' ' } == "Select Branch"
          ) {
            Toast.makeText(this, "Please select Branch", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "Branch Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString(
                          "branch", getSmallBranch(spinnerBranch.selectedItem.toString())
                      )
                      sharedPreferencesEdit.apply()
                      tv_branch.text = spinnerBranch.selectedItem.toString()
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["branch"] = getSmallBranch(spinnerBranch.selectedItem.toString())
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }

      edit_semester.setOnClickListener {
        editProfileDialog.select_subject.text = "Select New Semester"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.GONE
        editProfileDialog.radioGroup_gender.visibility = View.GONE
        editProfileDialog.sp_branch.visibility = View.GONE
        editProfileDialog.sp_semester.visibility = View.VISIBLE
        editProfileDialog.sp_college.visibility = View.GONE
        editProfileDialog.sp_university.visibility = View.GONE
        editProfileDialog.btn_submit.setOnClickListener {
          if (spinnerSemester.selectedItem.toString()
                  .trim { it <= ' ' } == "Select Semester"
          ) {
            Toast.makeText(this, "Please select semester", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "Semester Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString(
                          "semester",
                          getSmallSemester(spinnerSemester.selectedItem.toString())
                      )
                      sharedPreferencesEdit.apply()
                      tv_semester.text = spinnerSemester.selectedItem.toString()
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["semester"] = getSmallSemester(spinnerSemester.selectedItem.toString())
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }

      edit_college.setOnClickListener {
        editProfileDialog.select_subject.text = "Select New College"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.GONE
        editProfileDialog.radioGroup_gender.visibility = View.GONE
        editProfileDialog.sp_branch.visibility = View.GONE
        editProfileDialog.sp_semester.visibility = View.GONE
        editProfileDialog.sp_college.visibility = View.VISIBLE
        editProfileDialog.sp_university.visibility = View.GONE
        editProfileDialog.btn_submit.setOnClickListener {
          if (spinnerCollege.selectedItem.toString()
                  .trim { it <= ' ' } == "Select College"
          ) {
            Toast.makeText(this, "Please select college", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "College Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString(
                          "college", spinnerCollege.selectedItem.toString()
                      )
                      sharedPreferencesEdit.apply()
                      tv_college.text = spinnerCollege.selectedItem.toString()
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["college"] = spinnerCollege.selectedItem.toString()
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }

      edit_university.setOnClickListener {
        editProfileDialog.select_subject.text = "Select New University"
        editProfileDialog.show()
        editProfileDialog.et_name.visibility = View.GONE
        editProfileDialog.radioGroup_gender.visibility = View.GONE
        editProfileDialog.sp_branch.visibility = View.GONE
        editProfileDialog.sp_semester.visibility = View.GONE
        editProfileDialog.sp_college.visibility = View.GONE
        editProfileDialog.sp_university.visibility = View.VISIBLE
        editProfileDialog.btn_submit.setOnClickListener {
          if (spinnerUniversity.selectedItem.toString()
                  .trim { it <= ' ' } == "Select University"
          ) {
            Toast.makeText(this, "Please select University", Toast.LENGTH_SHORT)
                .show()
          } else {
            dialog.show()
            try {
              val url = ExtraFunctions.serverurl + "EditProfile.php"
              val stringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                  //                progressBar.setVisibility(View.GONE)
                  try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                      Toast.makeText(this, "University Changed", Toast.LENGTH_SHORT)
                          .show()
                      sharedPreferencesEdit.putString(
                          "university",
                          getSmallUniversity(spinnerUniversity.selectedItem.toString())
                      )

                      sharedPreferencesEdit.apply()
                      tv_university.text = spinnerUniversity.selectedItem.toString()
                      editProfileDialog.dismiss()
                      dialog.dismiss()
                    } else {
                      Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                          .show()
                      dialog.dismiss()
                    }
                  } catch (exception: Exception) {
                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                  }
                }, Response.ErrorListener {
                }) {
                  override fun getParams(): Map<String, String> {
                    val myData = HashMap<String, String>()
                    myData["userid"] = userid
                    myData["university"] =
                      getSmallUniversity(spinnerUniversity.selectedItem.toString())
                    return myData
                  }
                }
              requestQueue.add(stringRequest)
            } catch (e: java.lang.Exception) {
              Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT)
                  .show()
              dialog.dismiss()
            }
          }
        }
      }
    }

    //imagepicker
    ivChangeDp.setOnClickListener {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .start(this)
    }

    if (profile == "OtherProfile") {
      postByUserId = intent.getStringExtra("userid")
      postByUserName = intent.getStringExtra("username")
      postByUserDp = intent.getStringExtra("userdp")
      postByUserBranch = intent.getStringExtra("userbranch")
      supportActionBar!!.title = "$postByUserName's Profile"
      requestQueue.add(
          ExtraFunctions.createImageRequestFromUrl(
              ExtraFunctions.serverurl + "userdp/" + postByUserDp, iv_profile_image
          )
      )
      iv_profile_image.setOnClickListener {
        animateIntent(iv_profile_image)
      }
      tv_name.text = postByUserName
      tv_branch.text = getFullBranch(postByUserBranch)
      edit_name.visibility = View.GONE
      edit_gender.visibility = View.GONE
      edit_semester.visibility = View.GONE
      edit_branch.visibility = View.GONE
      edit_college.visibility = View.GONE
      edit_university.visibility = View.GONE
      iv_change_dp.visibility = View.GONE
      if (ExtraFunctions.isNetworkStatusAvailable(this)) {
        volleyPostDataRequest(postByUserId)
        volleyDoubtsPostsDataRequest(postByUserId)
        val url = ExtraFunctions.serverurl + "otherUsersProfileData.php"
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
          try {
            val emp = JSONObject(response)
            val userEmail: String = emp.getString("email")
            val userGender: String = emp.getString("gender")
            val userSemester: String = emp.getString("semester")
            val userCollege: String = emp.getString("college")
            val userUniversity: String = emp.getString("university")
            val userJoinDate: String = emp.getString("joindate")
            val userPosts: String = emp.getString("posts")
            val userDoubts: String = emp.getString("doubts")
            val userAnswers: String = emp.getString("answers")
            tv_email.text = userEmail
            tv_gender.text = userGender
            tv_semester.text = getFullSemester(userSemester)
            tv_college.text = userCollege
            tv_university.text = getFullUniversity(userUniversity)
            tv_active_since.text = userJoinDate
            tv_posts.text = userPosts
            tv_doubts.text = userDoubts
            tv_answers.text = userAnswers
            if (userGender == "Male") {
              tv_my_posts.text = "His Posts"
              tv_my_posts_doubts.text = "His Doubts"
            } else {
              tv_my_posts.text = "Her Posts"
              tv_my_posts_doubts.text = "Her Doubts"
            }
          } catch (exception: Exception) {

          }
        }, Response.ErrorListener {
          Toast.makeText(this, "Error! Please try again later...", Toast.LENGTH_SHORT)
              .show()
        }) {
          override fun getParams(): Map<String, String> {
            val myData = HashMap<String, String>()
            myData["userid"] = postByUserId
            return myData
          }
        }
        requestQueue.add(stringRequest)

      } else {
        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT)
            .show()
      }
    }
    toolbar_main.setNavigationOnClickListener {
      finish()
    }
    //posts
    useridlist = ArrayList()
    userdplist = ArrayList()
    usernamelist = ArrayList()
    posttimelist = ArrayList()
    postidlist = ArrayList()
    userbranchlist = ArrayList()
    posttextlist = ArrayList()
    postfilelist = ArrayList()
    subjectlist = ArrayList()
    //postdoubts
    useridpostlist = ArrayList()
    usernamepostlist = ArrayList()
    userdppostlist = ArrayList()
    posttimepostlist = ArrayList()
    userbranchpostlist = ArrayList()
    postdoubtidpostlist = ArrayList()
    posttextpostlist = ArrayList()
    postimagepostlist = ArrayList()
    likeslist = ArrayList<String>()
    commentslist = ArrayList<String>()
    postnoofanswerslist = ArrayList<String>()
  }

  private fun volleyPostDataRequest(userid: String) {
    try {
      val url = ExtraFunctions.serverurl + "postsProfileDataAdapter.php"
      val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
        progress_bar_profile.visibility = View.GONE
        try {
          val emp = JSONObject(response)
          val result = emp.getString("result")
          if (result == "successful") {
            useridlist.clear()
            usernamelist.clear()
            userdplist.clear()
            posttimelist.clear()
            userbranchlist.clear()
            postidlist.clear()
            posttextlist.clear()
            postfilelist.clear()
            subjectlist.clear()
            likeslist.clear()
            commentslist.clear()

            val useridarray = emp.getJSONArray("userid")
            val usernamearray = emp.getJSONArray("name")
            val userbrancharray = emp.getJSONArray("branch")
            val userdparray = emp.getJSONArray("userdp")
            val posttimearray = emp.getJSONArray("posttime")
            val postdoubtidarray = emp.getJSONArray("postid")
            val posttextarray = emp.getJSONArray("posttext")
            val postfilearray = emp.getJSONArray("filename")
            val postsubjectarray = emp.getJSONArray("subject")
            val postlikesarray = emp.getJSONArray("likes")
            val postcommentsarray = emp.getJSONArray("comments")

            if (useridarray != null) {
              val len = useridarray.length()
              for (i in 0 until len) {
                useridlist.add(
                    useridarray.get(i)
                        .toString()
                )
                usernamelist.add(
                    usernamearray.get(i)
                        .toString()
                )
                userdplist.add(
                    userdparray.get(i)
                        .toString()
                )
                posttimelist.add(
                    posttimearray.get(i)
                        .toString()
                )
                userbranchlist.add(
                    userbrancharray.get(i)
                        .toString()
                )
                postidlist.add(
                    postdoubtidarray.get(i)
                        .toString()
                )
                posttextlist.add(
                    posttextarray.get(i)
                        .toString()
                )
                postfilelist.add(
                    postfilearray.get(i)
                        .toString()
                )
                subjectlist.add(
                    postsubjectarray.get(i)
                        .toString()
                )
                likeslist.add(
                    postlikesarray.get(i)
                        .toString()
                )
                commentslist.add(
                    postcommentsarray.get(i)
                        .toString()
                )
              }
            }
            val profilePostsAdapter =
              MyRecyclerHomePostsAdapter(
                  sharedPreferences, sharedPreferencesLike, dialog, requestQueue,
                  this, userid, useridlist, userdplist, usernamelist,
                  userbranchlist, posttimelist, postfilelist, postidlist,
                  posttextlist, subjectlist, likeslist, commentslist
              )
            profilePostsAdapter.setHasStableIds(true)
            recyclerViewProfile.adapter = profilePostsAdapter
          }
        } catch (exception: Exception) {
          Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT)
              .show()
        }
      }, Response.ErrorListener {
        progress_bar_profile.visibility = View.GONE
        //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
      }) {
        override fun getParams(): Map<String, String> {
          val myData = HashMap<String, String>()
          myData["userid"] = userid
          return myData
        }
      }
      requestQueue.add(stringRequest)
    } catch (e: Exception) {
      Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT)
          .show()
    }

  }

  private fun volleyDoubtsPostsDataRequest(userid: String) {
    val url = ExtraFunctions.serverurl + "doubtPostsProfileDataAdapter.php"
    val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
      try {
        val emp = JSONObject(response)
        val result = emp.getString("result")
        if (result == "successful") {
          useridpostlist.clear()
          usernamepostlist.clear()
          userdppostlist.clear()
          posttimepostlist.clear()
          userbranchpostlist.clear()
          postdoubtidpostlist.clear()
          posttextpostlist.clear()
          postimagepostlist.clear()
          postnoofanswerslist.clear()

          val useridpostarray = emp.getJSONArray("userid")
          val usernamepostarray = emp.getJSONArray("name")
          val userbranchpostarray = emp.getJSONArray("branch")
          val userdppostarray = emp.getJSONArray("userdp")
          val posttimepostarray = emp.getJSONArray("posttime")
          val postdoubtidpostarray = emp.getJSONArray("postdoubtid")
          val posttextpostarray = emp.getJSONArray("posttext")
          val postimagepostarray = emp.getJSONArray("postimage")
          val postnoofanswersarray = emp.getJSONArray("answers")

          if (useridpostarray != null) {
            val len = useridpostarray.length()
            for (i in 0 until len) {
              useridpostlist.add(
                  useridpostarray.get(i)
                      .toString()
              )
              usernamepostlist.add(
                  usernamepostarray.get(i)
                      .toString()
              )
              userdppostlist.add(
                  userdppostarray.get(i)
                      .toString()
              )
              posttimepostlist.add(
                  posttimepostarray.get(i)
                      .toString()
              )
              userbranchpostlist.add(
                  userbranchpostarray.get(i)
                      .toString()
              )
              postdoubtidpostlist.add(
                  postdoubtidpostarray.get(i)
                      .toString()
              )
              posttextpostlist.add(
                  posttextpostarray.get(i)
                      .toString()
              )
              postimagepostlist.add(
                  postimagepostarray.get(i)
                      .toString()
              )
              postnoofanswerslist.add(
                  postnoofanswersarray.get(i)
                      .toString()
              )
            }
          }
          val postDoubtsProfileAdapter =
            MyRecyclerPostDoubtsAdapter(
                sharedPreferences, dialog, requestQueue, this, userid, useridpostlist,
                userdppostlist, usernamepostlist,
                userbranchpostlist, posttimepostlist, postimagepostlist,
                postdoubtidpostlist, posttextpostlist, postnoofanswerslist
            )
          postDoubtsProfileAdapter.setHasStableIds(true)
          recyclerViewProfilePostsDoubts.adapter = postDoubtsProfileAdapter
        }
      } catch (exception: Exception) {
        Toast.makeText(this, "some error occured! try again", Toast.LENGTH_SHORT)
            .show()
      }
    }, Response.ErrorListener {
      //                Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
    }) {
      override fun getParams(): Map<String, String> {
        val myData = HashMap<String, String>()
        myData["userid"] = userid
        return myData
      }
    }
    requestQueue.add(stringRequest)

  }

  public override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
      val result = CropImage.getActivityResult(data)
      if (resultCode == Activity.RESULT_OK) {
        val resultUri = result.uri
        val path: String = resultUri.path
        //now we have path of file we should compress image
        iv_profile_image.setImageBitmap(compressImage(path))
        volleyImageRequest()
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        //on error
        dialog.dismiss()
        Toast.makeText(this, "An error occured! Please try again later.", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  private fun compressImage(path: String?): Bitmap? {
    val imageData: ByteArray?
    return try {
      val THUMBNAIL_SIZE = 256
      val fis = FileInputStream(File(path!!))
      var imageBitmap = BitmapFactory.decodeStream(fis)
      imageBitmap = Bitmap.createScaledBitmap(
          imageBitmap, THUMBNAIL_SIZE,
          THUMBNAIL_SIZE, false
      )
      val baos = ByteArrayOutputStream()
      imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
      imageData = baos.toByteArray()
      BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
    } catch (ex: Exception) {
      null
    }

  }

  private fun volleyImageRequest() {
    val url = ExtraFunctions.serverurl + "ChangeProfilePicture.php"
    val multipartRequest =
      object : VolleyMultipartRequest(Method.POST, url, Response.Listener { response ->
        val resultResponse = String(response.data)
        try {
          val result = JSONObject(resultResponse)
          val status = result.getString("result")
          if (status == "successful") {
            dialog.dismiss()
            Toast.makeText(this@ProfileActivity, "DP Changed", Toast.LENGTH_SHORT)
                .show()
            requestQueue.add(
                ExtraFunctions.createImageRequestFromUrl(
                    ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image
                )
            )
            val sharedPreferencesEdit = sharedPreferences.edit()
            sharedPreferencesEdit.putBoolean("dataChange", true)
            sharedPreferencesEdit.apply()
          }
          if (status == "error") {
            dialog.dismiss()
            Toast.makeText(this@ProfileActivity, "Error! Please try again later...", Toast.LENGTH_SHORT)
                .show()
          }
        } catch (e: JSONException) {
          dialog.dismiss()
          e.printStackTrace()
        }
      }, Response.ErrorListener {
        dialog.dismiss()
        Toast.makeText(this@ProfileActivity, "Volley Error", Toast.LENGTH_SHORT)
            .show()
      }) {
        override fun getParams(): Map<String, String> {
          val params = HashMap<String, String>()
          params["userid"] = userid
          return params
        }

        override fun getByteData(): Map<String, DataPart>? {
          val params = HashMap<String, DataPart>()
          // file name could found file base or direct access from real path
          // for now just get bitmap data from ImageView
          params["profileimage"] = DataPart(
              "profile_image.jpg",
              AppHelper.getFileDataFromDrawable(
                  baseContext, iv_profile_image.getDrawable()
              ),
              "image/jpeg"
          )
          //DataPart second parameter is byte[]
          return params
        }
      }

    VolleySingleton.getInstance(baseContext)
        .addToRequestQueue(multipartRequest)
  }

  fun animateIntent(view: ImageView) {
    val intent = Intent(this, ImageViewerActivity::class.java)
    intent.putExtra("intentType", "byteArray")
    intent.putExtra(
        "imageByteArray",
        getFileDataFromDrawable(view.drawable)
    )
    val transitionName = getString(
        string.transition_string
    )

    val options =
      ActivityOptionsCompat.makeSceneTransitionAnimation(
          this,
          view, // Starting view
          transitionName    // The String
      )
    ActivityCompat.startActivity(this, intent, options.toBundle())
  }

  fun getFileDataFromDrawable(
    drawable: Drawable
  ): ByteArray {
    val bitmap = (drawable as BitmapDrawable).bitmap
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
  }

}

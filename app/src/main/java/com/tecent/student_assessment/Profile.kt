package com.tecent.student_assessment

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.custom_dialog_comments_reply.*
import kotlinx.android.synthetic.main.custom_dialog_edit_profile.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Profile : AppCompatActivity() {
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
    //doubts
    internal lateinit var useridpostlist: ArrayList<String>
    internal lateinit var usernamepostlist: ArrayList<String>
    internal lateinit var userdppostlist: ArrayList<String>
    internal lateinit var posttimepostlist: ArrayList<String>
    internal lateinit var userbranchpostlist: ArrayList<String>
    internal lateinit var postdoubtidpostlist: ArrayList<String>
    internal lateinit var posttextpostlist: ArrayList<String>
    internal lateinit var postimagepostlist: ArrayList<String>

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesLike: SharedPreferences
    lateinit var recyclerViewProfile: RecyclerView
    lateinit var recyclerViewProfilePostsDoubts: RecyclerView
    lateinit var requestQueue: RequestQueue
    lateinit var dialog: ACProgressFlower
    lateinit var userid: String
    lateinit var postByUserId: String
    lateinit var postByUserName: String
    lateinit var postByUserDp: String
    lateinit var postByUserBranch: String
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
        val profile: String = intent.getStringExtra("profile")
        recyclerViewProfile = findViewById<RecyclerView>(R.id.recyclerView_profile)
        recyclerViewProfile.setHasFixedSize(true)
        recyclerViewProfile.setLayoutManager(LinearLayoutManager(this))
        recyclerViewProfilePostsDoubts = findViewById<RecyclerView>(R.id.recyclerView_profile_doubts)
        recyclerViewProfilePostsDoubts.setHasFixedSize(true)
        recyclerViewProfilePostsDoubts.setLayoutManager(LinearLayoutManager(this))
        sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        sharedPreferencesLike = this.getSharedPreferences("postLikes", Context.MODE_PRIVATE)
        userid = this.sharedPreferences.getString("userid", "")
        requestQueue = Volley.newRequestQueue(this)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)
        if (profile == "MyProfile") {
            supportActionBar!!.setTitle("My Profile")
            val email: String = sharedPreferences.getString("email", "")
            val name: String = sharedPreferences.getString("name", "")
            val gender: String = sharedPreferences.getString("gender", "")
            val branch: String = sharedPreferences.getString("branch", "")
            val semester: String = sharedPreferences.getString("semester", "")
            val college: String = sharedPreferences.getString("college", "")
            val university: String = sharedPreferences.getString("university", "")
            val userdp: String = sharedPreferences.getString("userdp", "")
            val joindate: String = sharedPreferences.getString("joindate", "")
            val posts: String = sharedPreferences.getString("posts", "")
            val doubts: String = sharedPreferences.getString("doubts", "")
            val answers: String = sharedPreferences.getString("answers", "")
            requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image))
            tv_name.setText(name)
            tv_active_since.setText(joindate)
            tv_email.setText(email)
            tv_gender.setText(gender)
            tv_branch.setText(ExtraFunctions.getFullBranch(branch))
            tv_semester.setText(ExtraFunctions.getFullSemester(semester))
            tv_college.setText(college)
            tv_university.setText(ExtraFunctions.getFullUniversity(university))
            tv_posts.setText(posts)
            tv_doubts.setText(doubts)
            tv_answers.setText(answers)
            if (ExtraFunctions.isNetworkStatusAvialable(this)) {
                volleyPostDataRequest(userid)
                volleyDoubtsPostsDataRequest(userid)
            } else {
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
            val editProfileDialog = Dialog(this)
            editProfileDialog.setContentView(R.layout.custom_dialog_edit_profile)

            val spinnerBranch = editProfileDialog.findViewById<Spinner>(R.id.sp_branch)
            val valuesBranch = resources.getStringArray(R.array.branches)
            val adapterBranch = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesBranch)
            spinnerBranch.setAdapter(adapterBranch)

            val spinnerSemester = editProfileDialog.findViewById<Spinner>(R.id.sp_semester)
            val valuesSemester = resources.getStringArray(R.array.semester)
            val adapterSemester = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesSemester)
            spinnerSemester.setAdapter(adapterSemester)

            val spinnerCollege = editProfileDialog.findViewById<Spinner>(R.id.sp_college)
            val valuesCollege = resources.getStringArray(R.array.collegelist)
            val adapterCollege = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesCollege)
            spinnerCollege.setAdapter(adapterCollege)

            val spinnerUniversity = editProfileDialog.findViewById<Spinner>(R.id.sp_university)
            val valuesUniversity = resources.getStringArray(R.array.university)
            val adapterUniversity = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesUniversity)
            spinnerUniversity.setAdapter(adapterUniversity)

            editProfileDialog.et_name.visibility = View.GONE
            editProfileDialog.radioGroup_gender.visibility = View.GONE
            editProfileDialog.sp_branch.visibility = View.GONE
            editProfileDialog.sp_semester.visibility = View.GONE
            editProfileDialog.sp_college.visibility = View.GONE
            editProfileDialog.sp_university.visibility = View.GONE

            //edit name
            val sharedPreferencesEdit = sharedPreferences.edit()
            edit_name.setOnClickListener {
                editProfileDialog.select_subject.text="Enter New Name"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.VISIBLE
                editProfileDialog.radioGroup_gender.visibility = View.GONE
                editProfileDialog.sp_branch.visibility = View.GONE
                editProfileDialog.sp_semester.visibility = View.GONE
                editProfileDialog.sp_college.visibility = View.GONE
                editProfileDialog.sp_university.visibility = View.GONE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (editProfileDialog.et_name.text.toString().trim() == "") {
                        Toast.makeText(this, "Please write your new Name", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "Name Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("name", editProfileDialog.et_name.text.toString().trim())
                                        tv_name.text = editProfileDialog.et_name.text.toString().trim()
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["name"] = editProfileDialog.et_name.text.toString().trim()
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }


            edit_gender.setOnClickListener {
                editProfileDialog.select_subject.text="Enter New Gender"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.GONE
                editProfileDialog.radioGroup_gender.visibility = View.VISIBLE
                editProfileDialog.sp_branch.visibility = View.GONE
                editProfileDialog.sp_semester.visibility = View.GONE
                editProfileDialog.sp_college.visibility = View.GONE
                editProfileDialog.sp_university.visibility = View.GONE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (editProfileDialog.radioGroup_gender.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(this, "select your gender", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            var genderValue:String
                            if (editProfileDialog.rad_male.isChecked){
                                genderValue="Male"
                            }else if(editProfileDialog.rad_female.isChecked){
                                genderValue="Female"
                            }else{
                                genderValue="Others"
                            }
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "Gender Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("gender", genderValue)
                                        tv_gender.text = genderValue
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "volley error", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["gender"] = genderValue
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            edit_branch.setOnClickListener {
                editProfileDialog.select_subject.text="Select New Branch"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.GONE
                editProfileDialog.radioGroup_gender.visibility = View.GONE
                editProfileDialog.sp_branch.visibility = View.VISIBLE
                editProfileDialog.sp_semester.visibility = View.GONE
                editProfileDialog.sp_college.visibility = View.GONE
                editProfileDialog.sp_university.visibility = View.GONE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (spinnerBranch.selectedItem.toString().trim { it <= ' ' } == "Select Branch") {
                        Toast.makeText(this, "Please select Branch", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "Branch Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("branch", ExtraFunctions.getSmallBranch(spinnerBranch.selectedItem.toString()))
                                        tv_branch.text = spinnerBranch.selectedItem.toString()
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["branch"] = ExtraFunctions.getSmallBranch(spinnerBranch.selectedItem.toString())
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            edit_semester.setOnClickListener {
                editProfileDialog.select_subject.text="Select New Semester"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.GONE
                editProfileDialog.radioGroup_gender.visibility = View.GONE
                editProfileDialog.sp_branch.visibility = View.GONE
                editProfileDialog.sp_semester.visibility = View.VISIBLE
                editProfileDialog.sp_college.visibility = View.GONE
                editProfileDialog.sp_university.visibility = View.GONE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (spinnerSemester.selectedItem.toString().trim { it <= ' ' } == "Select Semester") {
                        Toast.makeText(this, "Please select semester", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "Semester Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("semester", ExtraFunctions.getSmallSemester(spinnerSemester.selectedItem.toString()))
                                        tv_semester.text = spinnerSemester.selectedItem.toString()
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["semester"] = ExtraFunctions.getSmallSemester(spinnerSemester.selectedItem.toString())
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            edit_college.setOnClickListener {
                editProfileDialog.select_subject.text="Select New College"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.GONE
                editProfileDialog.radioGroup_gender.visibility = View.GONE
                editProfileDialog.sp_branch.visibility = View.GONE
                editProfileDialog.sp_semester.visibility = View.GONE
                editProfileDialog.sp_college.visibility = View.VISIBLE
                editProfileDialog.sp_university.visibility = View.GONE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (spinnerCollege.selectedItem.toString().trim { it <= ' ' } == "Select College") {
                        Toast.makeText(this, "Please select college", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "College Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("college", spinnerCollege.selectedItem.toString())
                                        tv_college.text = spinnerCollege.selectedItem.toString()
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["college"] = spinnerCollege.selectedItem.toString()
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            edit_university.setOnClickListener {
                editProfileDialog.select_subject.text="Select New University"
                editProfileDialog.show()
                editProfileDialog.et_name.visibility = View.GONE
                editProfileDialog.radioGroup_gender.visibility = View.GONE
                editProfileDialog.sp_branch.visibility = View.GONE
                editProfileDialog.sp_semester.visibility = View.GONE
                editProfileDialog.sp_college.visibility = View.GONE
                editProfileDialog.sp_university.visibility = View.VISIBLE
                editProfileDialog.btn_submit.setOnClickListener {
                    if (spinnerUniversity.selectedItem.toString().trim { it <= ' ' } == "Select University") {
                        Toast.makeText(this, "Please select University", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.show()
                        try {
                            val url = ExtraFunctions.serverurl + "EditProfile.php"
                            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                                //                progressBar.setVisibility(View.GONE)
                                try {
                                    val emp = JSONObject(response)
                                    val result = emp.getString("result")
                                    if (result == "successful") {
                                        Toast.makeText(this, "University Changed", Toast.LENGTH_SHORT).show()
                                        sharedPreferencesEdit.putString("university", ExtraFunctions.getSmallUniversity(spinnerUniversity.selectedItem.toString()))
                                        tv_university.text = spinnerUniversity.selectedItem.toString()
                                        editProfileDialog.dismiss()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                } catch (exception: Exception) {
                                    Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }, Response.ErrorListener {
                            }) {
                                override fun getParams(): Map<String, String> {
                                    val MyData = HashMap<String, String>()
                                    MyData["userid"] = userid
                                    MyData["university"] = ExtraFunctions.getSmallUniversity(spinnerUniversity.selectedItem.toString())
                                    return MyData
                                }
                            }
                            requestQueue.add(stringRequest)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
        if (profile == "OtherProfile") {
            postByUserId = intent.getStringExtra("userid")
            postByUserName = intent.getStringExtra("username")
            postByUserDp = intent.getStringExtra("userdp")
            postByUserBranch = intent.getStringExtra("userbranch")
            supportActionBar!!.setTitle(postByUserName + "'s Profile")
            requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + postByUserDp, iv_profile_image))
            tv_name.setText(postByUserName)
            tv_branch.setText(ExtraFunctions.getFullBranch(postByUserBranch))
            edit_name.visibility = View.GONE
            edit_gender.visibility = View.GONE
            edit_semester.visibility = View.GONE
            edit_branch.visibility = View.GONE
            edit_college.visibility = View.GONE
            edit_university.visibility = View.GONE
            if (ExtraFunctions.isNetworkStatusAvialable(this)) {
                volleyPostDataRequest(postByUserId)
                volleyDoubtsPostsDataRequest(postByUserId)
                val url = ExtraFunctions.serverurl + "otherUsersProfileData.php"
                val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                    try {
                        val emp = JSONObject(response)
                        val userEmail: String = emp.getString("email")
                        val userGender: String = emp.getString("gender")
                        val userSemester: String = emp.getString("semester")
                        val userCollege: String = emp.getString("college")
                        val userUniversity: String = emp.getString("university")
                        val userJoindate: String = emp.getString("joindate")
                        val userPosts: String = emp.getString("posts")
                        val userDoubts: String = emp.getString("doubts")
                        val userAnswers: String = emp.getString("answers")
                        tv_email.setText(userEmail)
                        tv_gender.setText(userGender)
                        tv_semester.setText(ExtraFunctions.getFullSemester(userSemester))
                        tv_college.setText(userCollege)
                        tv_university.setText(ExtraFunctions.getFullUniversity(userUniversity))
                        tv_active_since.setText(userJoindate)
                        tv_posts.setText(userPosts)
                        tv_doubts.setText(userDoubts)
                        tv_answers.setText(userAnswers)
                        if (userGender == "Male") {
                            tv_my_posts.setText("His Posts")
                            tv_my_posts_doubts.setText("His Doubts")
                        } else {
                            tv_my_posts.setText("Her Posts")
                            tv_my_posts_doubts.setText("Her Doubts")
                        }
                    } catch (exception: Exception) {

                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val MyData = HashMap<String, String>()
                        MyData["userid"] = postByUserId
                        return MyData
                    }
                }
                requestQueue.add(stringRequest)

            } else {
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
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
    }

    fun volleyPostDataRequest(userid: String) {
        try {
            val url = ExtraFunctions.serverurl + "postsProfileDataAdapter.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                progress_bar_profile.setVisibility(View.GONE)
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

                        val useridarray = emp.getJSONArray("userid")
                        val usernamearray = emp.getJSONArray("name")
                        val userbrancharray = emp.getJSONArray("branch")
                        val userdparray = emp.getJSONArray("userdp")
                        val posttimearray = emp.getJSONArray("posttime")
                        val postdoubtidarray = emp.getJSONArray("postid")
                        val posttextarray = emp.getJSONArray("posttext")
                        val postfilearray = emp.getJSONArray("filename")
                        val postsubjectarray = emp.getJSONArray("subject")

                        if (useridarray != null) {
                            val len = useridarray.length()
                            for (i in 0 until len) {
                                useridlist.add(useridarray.get(i).toString())
                                usernamelist.add(usernamearray.get(i).toString())
                                userdplist.add(userdparray.get(i).toString())
                                posttimelist.add(posttimearray.get(i).toString())
                                userbranchlist.add(userbrancharray.get(i).toString())
                                postidlist.add(postdoubtidarray.get(i).toString())
                                posttextlist.add(posttextarray.get(i).toString())
                                postfilelist.add(postfilearray.get(i).toString())
                                subjectlist.add(postsubjectarray.get(i).toString())
                            }
                        }
                        val profilePostsAdapter = MyRecyclerHomePostsAdapter(sharedPreferencesLike, dialog, requestQueue, this, userid, useridlist, userdplist, usernamelist,
                                userbranchlist, posttimelist, postfilelist, postidlist, posttextlist, subjectlist)
                        recyclerViewProfile.adapter = profilePostsAdapter
                    }
                } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                progress_bar_profile.setVisibility(View.GONE)
                //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    MyData["userid"] = userid
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun volleyDoubtsPostsDataRequest(userid: String) {
        val url = ExtraFunctions.serverurl + "doubtPostsProfileDataAdapter.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
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

                    val useridpostarray = emp.getJSONArray("userid")
                    val usernamepostarray = emp.getJSONArray("name")
                    val userbranchpostarray = emp.getJSONArray("branch")
                    val userdppostarray = emp.getJSONArray("userdp")
                    val posttimepostarray = emp.getJSONArray("posttime")
                    val postdoubtidpostarray = emp.getJSONArray("postdoubtid")
                    val posttextpostarray = emp.getJSONArray("posttext")
                    val postimagepostarray = emp.getJSONArray("postimage")

                    if (useridpostarray != null) {
                        val len = useridpostarray.length()
                        for (i in 0 until len) {
                            useridpostlist.add(useridpostarray.get(i).toString())
                            usernamepostlist.add(usernamepostarray.get(i).toString())
                            userdppostlist.add(userdppostarray.get(i).toString())
                            posttimepostlist.add(posttimepostarray.get(i).toString())
                            userbranchpostlist.add(userbranchpostarray.get(i).toString())
                            postdoubtidpostlist.add(postdoubtidpostarray.get(i).toString())
                            posttextpostlist.add(posttextpostarray.get(i).toString())
                            postimagepostlist.add(postimagepostarray.get(i).toString())
                        }
                    }
                    val postDoubtsProfileAdapter = MyRecyclerPostDoubtsAdapter(dialog, requestQueue, this, userid, useridpostlist, userdppostlist, usernamepostlist,
                            userbranchpostlist, posttimepostlist, postimagepostlist, postdoubtidpostlist, posttextpostlist)
                    recyclerViewProfilePostsDoubts.adapter = postDoubtsProfileAdapter
                }
            } catch (exception: Exception) {
                Toast.makeText(this, "some error occured! try again", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            //                Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["userid"] = userid
                return MyData
            }
        }
        requestQueue.add(stringRequest)

    }

}

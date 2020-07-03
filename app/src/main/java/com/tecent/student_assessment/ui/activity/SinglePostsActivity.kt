package com.tecent.student_assessment.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tecent.student_assessment.objects.CommentObject
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsCommentsForSingleActivityAdapter
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.activity_profile.iv_profile_image
import kotlinx.android.synthetic.main.activity_single_posts.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SinglePostsActivity : AppCompatActivity() {

    lateinit var requestQueue: RequestQueue
    lateinit var dialog: ACProgressFlower
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerViewSinglePost: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_single_posts
        )
        setSupportActionBar(findViewById(
            id.toolbar_main
        ))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            drawable.ic_001_back
        )
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Post")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        sharedPreferences = this.getSharedPreferences(
            ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE)
        val intent = intent
        val postid:String=intent.getStringExtra("postid")
        requestQueue = Volley.newRequestQueue(this)
        recyclerViewSinglePost = findViewById<RecyclerView>(
            id.recyclerViewSinglePost
        )
        recyclerViewSinglePost.setHasFixedSize(true)
        recyclerViewSinglePost.setLayoutManager(
            LinearLayoutManager(this)
        )
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE).text("Uploading....")
                .fadeColor(Color.WHITE).build()
        volleySinglePostDataRequest(postid)
        volleyCommentDataRequest(postid)
        //long press click copy text
        iv_post_text.setOnLongClickListener {
            var cm:ClipboardManager= getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip:ClipData=ClipData.newPlainText(iv_post_text.text.toString(),iv_post_text.text)
            cm.primaryClip=clip
        Toast.makeText(this, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    fun volleySinglePostDataRequest(postid: String)
    {
        val url = ExtraFunctions.serverurl + "SinglePostsData.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            try {
                val emp = JSONObject(response)
                val userdp:String=emp.getString("userdp")
                val username:String=emp.getString("name")
                val userbranch:String=emp.getString("branch")
                val userPostTime: String = emp.getString("posttime")
                val userPostSubject: String = emp.getString("subject")
                val userPostText: String = emp.getString("posttext")
                val userfilename: String = emp.getString("filename")
                requestQueue.add<Bitmap>(
                    ExtraFunctions.createImageRequestFromUrl(
                        ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image))
                ivusername.setText(username)
                iv_datetime_branch_subject.setText(userPostTime + "  " + "\u2022" + " " + userbranch.toUpperCase() + "  " + "\u2022" + " " + userPostSubject)
                iv_post_text.setText(userPostText)
                if (userfilename != "") {
                    if (userfilename.substring(userfilename.lastIndexOf('.') + 1) == "pdf" || userfilename.substring(userfilename.lastIndexOf('.') + 1) == "PDF") {
                        requestQueue.add(
                            ExtraFunctions.createImageRequestFromUrl(
                                ExtraFunctions.serverurl +
                                "posts/pdfthumbnail/" + userfilename.replace(userfilename.substring(userfilename.lastIndexOf('.') + 1), "") + "jpg", iv_post_image))
                    } else {
                        requestQueue.add(
                            ExtraFunctions.createImageRequestFromUrl(
                                ExtraFunctions.serverurl + "posts/" + userfilename, iv_post_image))
                    }
                } else {
                    iv_post_image.setVisibility(View.GONE)
                }
            } catch (exception: Exception) {

            }
        }, Response.ErrorListener {
            Toast.makeText(this, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["postid"] = postid
                return MyData
            }
        }
        requestQueue.add(stringRequest)
    }

    fun volleyCommentDataRequest(postid:String){
        try {
            val url = ExtraFunctions.serverurl + "commentsPostsDataAdapter.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
//                progressBar.setVisibility(View.GONE)
                try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    if (result == "successful") {
                        val json = emp.getString("commentList")
                        val builder = GsonBuilder()
                        val gson = builder.create()
                        val commentObjectArrayList:ArrayList<CommentObject> = gson.fromJson(
                                json,
                                object : TypeToken<List<CommentObject>>() {
                                }.type
                        )
                        val adapter =
                            MyRecyclerHomePostsCommentsForSingleActivityAdapter(
                                dialog, requestQueue, postid,
                                sharedPreferences.getString("userid", ""), this
                                , commentObjectArrayList
                            )
                        adapter.setHasStableIds(true)
                        recyclerViewSinglePost.adapter = adapter
                    }
                } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
//                progressBar.setVisibility(View.GONE)
                //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    MyData["postid"] = postid
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}

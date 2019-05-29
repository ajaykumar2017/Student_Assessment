package com.tecent.student_assessment

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.iv_profile_image
import kotlinx.android.synthetic.main.activity_single_posts.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject
import java.util.HashMap

class SinglePostsActivity : AppCompatActivity() {

    lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_posts)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Posts")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        val intent = intent
        val postid:String=intent.getStringExtra("postid")
        requestQueue = Volley.newRequestQueue(this)

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
                requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, iv_profile_image))
                ivusername.setText(username)
                iv_datetime_branch_subject.setText(userPostTime + "  " + "\u2022" + " " + userbranch.toUpperCase() + "  " + "\u2022" + " " + userPostSubject)
                iv_post_text.setText(userPostText)
                if (userfilename != "") {
                    if (userfilename.substring(userfilename.lastIndexOf('.') + 1) == "pdf" || userfilename.substring(userfilename.lastIndexOf('.') + 1) == "PDF") {
                        requestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl +
                                "posts/pdfthumbnail/" + userfilename.replace(userfilename.substring(userfilename.lastIndexOf('.') + 1), "") + "jpg", iv_post_image))
                    } else {
                        requestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "posts/" + userfilename, iv_post_image))
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
}

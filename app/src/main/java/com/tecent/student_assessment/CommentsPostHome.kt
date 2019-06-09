package com.tecent.student_assessment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_comments_post_home.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.HashMap

class CommentsPostHome : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    lateinit var sharedPreferences: SharedPreferences
    lateinit var dialog: ACProgressFlower
    lateinit var userid: String
    lateinit var commentText: String
    var path: String=""
    lateinit var postid: String
    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var tv_btn_post_comment: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments_post_home)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Comments")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("userid", "")
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout)
        recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val userdp = sharedPreferences.getString("userdp", "")
        commentText = ""
        val intentPost = intent
        postid = intentPost.getStringExtra("postid")
        tv_btn_post_comment = findViewById<TextView>(R.id.tv_btn_post_comment)
        requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, imageView_user_image))
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE).text("Uploading....")
                .fadeColor(Color.WHITE).build()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (editText.text.toString().trim { it <= ' ' }.length >= 1) {
                    tv_btn_post_comment.setTextColor(ContextCompat.getColor(this@CommentsPostHome, R.color.colorPrimary))
                } else if (editText.text.toString().trim { it <= ' ' }.length < 1) {
                    tv_btn_post_comment.setTextColor(ContextCompat.getColor(this@CommentsPostHome, R.color.lightenblue))
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        tv_btn_post_comment.setOnClickListener {
            commentText = editText.text.toString().replace("'", "\\'")
            if (path!!.startsWith("/storage/primary/"))
                path = path!!.replace("/storage/primary/", ExtraFunctions.ROOTMAIN)
            val file = File(path)
                if (commentText.trim().length < 1) {
                    Toast.makeText(this, "please write something....", Toast.LENGTH_SHORT).show()
                } else if (file.exists()) {
                volleyImageRequest()
                    dialog.show()
                } else{
                volleyTestWithoutImage()
                    dialog.show()
                }
        }
        volleyCommentDataRequest(postid)

        swipeRefreshLayout.setOnRefreshListener {
            if (ExtraFunctions.isNetworkStatusAvialable(this))
                volleyCommentDataRequest(postid)
            else {
                swipeRefreshLayout.isRefreshing = false
//                progressBar.setVisibility(View.GONE)
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //image picker
    fun openImagePicker(view: View) {
        CropImage.activity().setAspectRatio(1, 1)
                .start(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                path = resultUri.path
                //now we have path of file we should compress image
                imageView3.setImageBitmap(compressImage(path))
                val paths = path!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//               textView3.setText(paths[paths.size - 1])
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //on error
                dialog.dismiss()
                Toast.makeText(this, "An error occured! Please try again later.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun compressImage(path: String?): Bitmap? {
        var imageData: ByteArray? = null
        try {
            val THUMBNAIL_SIZE = 256
            val fis = FileInputStream(File(path!!))
            var imageBitmap = BitmapFactory.decodeStream(fis)
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE, false)
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            imageData = baos.toByteArray()
            return BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
        } catch (ex: Exception) {
            return null
        }

    }

    fun volleyImageRequest() {
        val url = ExtraFunctions.serverurl + "postCommentsData.php"
        val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, url, Response.Listener { response ->
            val resultResponse = String(response.data)
            try {
                val result = JSONObject(resultResponse)
                val status = result.getString("result")
                if (status == "successful") {
                    dialog.dismiss()
                    editText.setText("")
                    Toast.makeText(this@CommentsPostHome, "Comment uploaded successfully", Toast.LENGTH_SHORT).show()
                    volleyCommentDataRequest(postid)
                }
                if (status == "error") {
                    dialog.dismiss()
                    Toast.makeText(this@CommentsPostHome, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                dialog.dismiss()
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@CommentsPostHome, "Volley Error", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userid
                params["postid"] = postid
                params["withimage"] = "yes"
                params["commenttext"] = editText.text.toString().replace("'", "\\'")
                return params
            }

            override fun getByteData(): Map<String, DataPart>? {
                val params = HashMap<String, DataPart>()
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params["commentimage"] = DataPart("comment_image.jpg", AppHelper.getFileDataFromDrawable(baseContext, imageView3.getDrawable()), "image/jpeg")
                //DataPart second parameter is byte[]
                return params
            }
        }

        VolleySingleton.getInstance(baseContext).addToRequestQueue(multipartRequest)
    }

    //Google volley
    fun volleyTestWithoutImage() {
        val url = ExtraFunctions.serverurl + "postCommentsData.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener { error ->
            dialog.dismiss()
            Toast.makeText(this@CommentsPostHome, error.toString(), Toast.LENGTH_SHORT).show()
            //                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["userid"] = userid
                MyData["postid"] = postid
                MyData["withimage"] = "no"
                MyData["commenttext"] = editText.text.toString().replace("'", "\\'")
                return MyData
            }
        }
        requestQueue.add(stringRequest)
    }

    fun jsonParser(jsontext: String) {
        try {
            val emp = JSONObject(jsontext)
            val result = emp.getString("result")
            if (result == "successful") {
                editText.setText("")
                dialog.dismiss()
                Toast.makeText(this@CommentsPostHome, "Comment uploaded successfully", Toast.LENGTH_SHORT).show()
                volleyCommentDataRequest(postid)
            }
            if (result == "error") {
                dialog.dismiss()
                Toast.makeText(this@CommentsPostHome, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            dialog.dismiss()
            exception.printStackTrace()
        }

    }
    //Comments data request
    fun volleyCommentDataRequest(postid:String){
        try {
            val url = ExtraFunctions.serverurl + "commentsPostsDataAdapter.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                swipeRefreshLayout.isRefreshing = false
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
                        val adapter = MyRecyclerPostsCommentsAdapter(
                                dialog, requestQueue,  postid, userid, this
                                ,commentObjectArrayList
                        )
                        recyclerView.adapter = adapter
                        swipeRefreshLayout.isRefreshing=false
                    }
                } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                swipeRefreshLayout.isRefreshing = false
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
//    public override fun onResume() {
//        volleyCommentDataRequest(postid)
//        super.onResume()
//    }

}

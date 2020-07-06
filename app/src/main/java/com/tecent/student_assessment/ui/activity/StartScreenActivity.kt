package com.tecent.student_assessment.ui.activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.app.Activity
import android.os.Build
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.ExtraFunctions

class StartScreenActivity : AppCompatActivity() {

    var backPressed=false
    internal lateinit var sharedPreferences: SharedPreferences
    private val mContext = this@StartScreenActivity

    private val REQUEST = 112

    internal lateinit var dialog:ACProgressFlower
    internal lateinit var requestQueue:RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_start_screen
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //Prevent landscape mode
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(this)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build()

        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSIONS = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions(mContext as Activity, PERMISSIONS, REQUEST)
            } else {
                moveForward()
            }
        } else {
            moveForward()
        }

    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveForward()
                } else {
                    Toast.makeText(mContext, "The app was not allowed to write in your storage", Toast.LENGTH_LONG)
                            .show()
                    finish()
                }
            }
        }
    }

    fun moveForward(){
        if(sharedPreferences.getString("login","")=="true") {
            val extras = intent.extras
            if (extras != null) {
                if (extras.containsKey("clickAction")&&extras.containsKey("linkUrl")) {
                    try{
                        when(extras.getString("clickAction")){
                            "viewNotice"->{
                                val url = extras.getString("linkUrl")!!
                                if (url.contains("sas.a3creators.co.in/StudentAssessment/post")) {
                                    val indexOfId = url.indexOf("id=")
                                    val postId = url.substring(indexOfId + 3)
                                    if (ExtraFunctions.isValidPostId(postId)) {
                                        getPostFromServer(postId)
                                    }
                                } else {
                                    moveForwardNormal()
                                }
                            }
                            "viewNewNotice"->{
                                val url:String = extras.getString("linkUrl")!!
                                if (url.contains("sas.a3creators.co.in/StudentAssessment/post")) {
                                    val indexOfId = url.indexOf("id=")
                                    val postId = url.substring(indexOfId + 3)
                                    if (ExtraFunctions.isValidPostId(postId)) {
                                        getPostFromServer(postId)
                                    }
                                } else {
                                    moveForwardNormal()
                                }
                            }
                            else->{
                                moveForwardNormal()
                            }
                        }
                    }catch (e:Exception){
                        moveForwardNormal()
                    }
                } else {
                    try {
                        val url = this.intent.data.toString()
                        if (url.contains("sas.a3creators.co.in/StudentAssessment/post")) {
                            val indexOfId = url.indexOf("id=")
                            val postId = url.substring(indexOfId + 3)
                            if (ExtraFunctions.isValidPostId(postId)) {
                                getPostFromServer(postId)
                            }
                        } else {
                            Log.d("iiii", "2")
                            moveForwardNormal()
                        }
                    } catch (e: Exception) {
                        Log.d("iiii", "3")
                        moveForwardNormal()
                    }
                }
            } else {
                Log.d("iiii", "4")
                moveForwardNormal()
            }
        }else{
            moveForwardNormal()
        }
    }

    fun moveForwardNormal(){
        Handler().postDelayed({
            if (!backPressed) {
                if (sharedPreferences.getString("login", "") == "true") {
                    startActivity(Intent(this@StartScreenActivity, HomeActivity::class.java))
                } else {
                    startActivity(Intent(this@StartScreenActivity, StudentLoginActivity::class.java))
                }
                finish()
            }
        }, 2000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backPressed = true
    }

    fun getPostFromServer(postId: String){

        Handler().postDelayed({
            val intent = Intent(this,
                SinglePostsActivity::class.java)
            intent.putExtra("postid",postId)
            startActivity(intent)
        }, 2000)
    }

    fun makeToast(toast: String, length: Int) {
        Toast.makeText(this, toast, length).show()
    }
}
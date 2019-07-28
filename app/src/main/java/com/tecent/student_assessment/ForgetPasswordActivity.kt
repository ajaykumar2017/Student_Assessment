package com.tecent.student_assessment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject

class ForgetPasswordActivity : AppCompatActivity() {
    var passwordInvisible:Boolean=true
    var confirmPasswordInvisible:Boolean=true
    var otp="124321"

    internal lateinit var sharedPreferences: SharedPreferences
    internal lateinit var dialog: ACProgressFlower
    internal lateinit var requestQueue: RequestQueue
    private fun makeToast(toast:String, length:Int){ Toast.makeText(this,toast,length).show() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.title = "Forget Password"
        toolbar_main.setNavigationOnClickListener {
            var intent: Intent = Intent(this,StudentLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //Prevent landscape mode
        sharedPreferences = getSharedPreferences(ExtraFunctions.sharedPreferencesId,
                Context.MODE_PRIVATE)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLACK).text("Please Wait..")
                .fadeColor(Color.BLUE).build()
        requestQueue = Volley.newRequestQueue(this)
        etPassword.setOnTouchListener { view: View, event: MotionEvent ->
            if(event.action== MotionEvent.ACTION_UP){
                if(event.rawX >= (etPassword.right - etPassword.compoundDrawables[2].bounds.width())){
                    if(passwordInvisible){
                        passwordInvisible=false
                        etPassword.transformationMethod=null
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_invisible_eye,0)
                    }else{
                        passwordInvisible=true
                        etPassword.transformationMethod= PasswordTransformationMethod()
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_visible_eye,0)
                    }
                }
            }
            false
        }  //to show or hide password when clicking on eye icon

        etConfirmPassword.setOnTouchListener { view: View, event: MotionEvent ->
            if(event.action== MotionEvent.ACTION_UP){
                if(event.rawX >= (etConfirmPassword.right - etConfirmPassword.compoundDrawables[2].bounds.width())){
                    if(confirmPasswordInvisible){
                        confirmPasswordInvisible=false
                        etConfirmPassword.transformationMethod=null
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_invisible_eye,0)
                    }else{
                        confirmPasswordInvisible=true
                        etConfirmPassword.transformationMethod= PasswordTransformationMethod()
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_visible_eye,0)
                    }
                }
            }
            false
        }  //to show or hide password when clicking on eye icon

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels

        etOTP.translationY=height.toFloat()
        btnVerifyOtp.translationY=height.toFloat()
        etPassword.translationY=height.toFloat()
        etConfirmPassword.translationY=height.toFloat()
        btnSubmit.translationY=height.toFloat()
    }



    fun requestOTP(view: View){
        if (ExtraFunctions.isValidEmailId(etEmail.text.toString())){
            dialog.show()
            val url = ExtraFunctions.serverurl + "checkEmail.php" // <----enter your post url here
            val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response->
                dialog.dismiss()
                try {
                    val emp = JSONObject(response)
                    when(emp.getString("result")){
                        "successful"->{
                            otp=emp.getString("otp")
                            etEmail.isEnabled=false
                            btnRequestOtp.text="Resend OTP"
                            etOTP.animate().translationY(0f)
                            btnVerifyOtp.animate().translationY(0f)
                        }
                        "email not registered"->{
                            makeToast("Email not registered.", Toast.LENGTH_LONG)
                        }
                        else->{
                            makeToast("Error! Please try again.", Toast.LENGTH_LONG)
                        }
                    }
                } catch (e: Exception) {
                    makeToast("Error! Please try again.", Toast.LENGTH_LONG)
                }
            }, Response.ErrorListener {
                dialog.dismiss()
                makeToast("Error! Please try again.", Toast.LENGTH_LONG)
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    MyData["email"] = etEmail.text.toString()
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        }else{
            makeToast("Enter valid email id",Toast.LENGTH_LONG)
        }
    }

    fun verifyOtp(view: View){
        dialog.show()
        Handler().postDelayed(Runnable {
            dialog.dismiss()
            if (etOTP.text.toString()==otp){
                btnRequestOtp.isEnabled=false
                btnRequestOtp.visibility=View.GONE
                etOTP.isEnabled=false
                btnVerifyOtp.isEnabled=false
                btnVerifyOtp.visibility=View.GONE
                etPassword.animate().translationY(0f)
                etConfirmPassword.animate().translationY(0f)
                btnSubmit.animate().translationY(0f)
            }else{
                makeToast("Incorrect OTP",Toast.LENGTH_LONG)
            }
        },3000)
    }

    fun createNewPassword(view: View){
        if(etPassword.text.toString().trim().length<8||etPassword.text.toString().trim().length>32){
            makeToast("Invalid Password",Toast.LENGTH_LONG)
        }else if(etPassword.text.toString()!=etConfirmPassword.text.toString()){
            makeToast("Password Mismatch",Toast.LENGTH_LONG)
        }else{
            dialog.show()
            val url = ExtraFunctions.serverurl + "changePassword.php" // <----enter your post url here
            val stringRequest = object : StringRequest(
                    Method.POST, url, Response.Listener { response->
                dialog.dismiss()
                try {
                    val emp = JSONObject(response)
                    when(emp.getString("result")){
                        "successful"->{
                            makeToast("Password changed successfully.", Toast.LENGTH_LONG)
                            var intent1: Intent = Intent(this,StudentLoginActivity::class.java)
                            startActivity(intent1)
                            finish()
                        }
                        else->{
                            makeToast("Error! Please try again.", Toast.LENGTH_LONG)
                        }
                    }
                } catch (e: Exception) {
                    makeToast("Error! Please try again.", Toast.LENGTH_LONG)
                }
            }, Response.ErrorListener {
                dialog.dismiss()
                makeToast("Error! Please try again.", Toast.LENGTH_LONG)
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    MyData["email"] = etEmail.text.toString()
                    MyData["password"] = etPassword.text.toString()
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        }
    }

    override fun onBackPressed() {
        var intent: Intent = Intent(this,StudentLoginActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}

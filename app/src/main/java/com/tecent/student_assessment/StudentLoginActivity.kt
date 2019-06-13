package com.tecent.student_assessment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject

import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import kotlinx.android.synthetic.main.activity_student_login.*

class StudentLoginActivity : AppCompatActivity() {

    lateinit var emailId: EditText
    lateinit var password: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var dialog: ACProgressFlower
    lateinit var requestQueue: RequestQueue
    var passwordInvisible:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)


        emailId = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Please wait....")
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)
        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        //view password
        et_password.setOnTouchListener { view: View, event: MotionEvent ->
            if(event.action==MotionEvent.ACTION_UP){
                if(event.rawX >= (et_password.right - et_password.compoundDrawables[2].bounds.width())){
                    if(passwordInvisible){
                        passwordInvisible=false
                        et_password.transformationMethod=null
                        et_password.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_invisible_eye,0)
                    }else{
                        passwordInvisible=true
                        et_password.transformationMethod= PasswordTransformationMethod()
                        et_password.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_visible_eye,0)
                    }
                    true
                }
            }
            false
        }  //to show or hide password when clicking on eye icon

    }

    fun loginVerify(view: View) {
        val email = emailId.text.toString().trim { it <= ' ' }
        val passw = password.text.toString().trim { it <= ' ' }
        if (!(email == "" || passw == "")) {
            if (ExtraFunctions.isNetworkStatusAvialable(this@StudentLoginActivity)) {
                try {
                    dialog.show()
                    volleyLoginData(email, passw)
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(this@StudentLoginActivity, "Error! Please try again later.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this@StudentLoginActivity, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid Credientials", Toast.LENGTH_SHORT).show()
        }

    }

    //google volley part start
    fun volleyLoginData(emailValue: String, passwordValue: String) {
        val url = ExtraFunctions.serverurl + "studentLoginVerify.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> jsonParser(response, emailValue, passwordValue) }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@StudentLoginActivity, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["email"] = emailValue
                MyData["passw"] = passwordValue
                return MyData
            }
        }
        requestQueue.add(stringRequest)

        //volley part start

    }

    fun jsonParser(jsontext: String, emailText: String, passwordText: String) {
        try {
            val emp = JSONObject(jsontext)
            val result = emp.getString("result")
            if (result == "Email not Registered") {
                dialog.dismiss()
                Toast.makeText(this@StudentLoginActivity, "Email-ID not registered", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({
                    val intentNew = Intent(this@StudentLoginActivity, StudentRegistration::class.java)
                    startActivity(intentNew)
                    finish()
                }, 1000)
            } else if (result == "Successful") {
                dialog.dismiss()
                Toast.makeText(this@StudentLoginActivity, "Login successful.", Toast.LENGTH_SHORT).show()
                val spe = sharedPreferences.edit()
                spe.putString("email", emailText)
                spe.putString("passw", passwordText)
                spe.putString("login", "true")
                spe.commit()
                Handler().postDelayed({
                    val intentNew = Intent(this@StudentLoginActivity, SetUpActivity::class.java)
                    startActivity(intentNew)
                    finish()
                }, 100)
            } else if (result == "Invalid password") {
                dialog.dismiss()
                Toast.makeText(this@StudentLoginActivity, "Invalid Password. Please try again", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            dialog.dismiss()
            exception.printStackTrace()
        }

    }

    fun register(view: View) {
        val intent = Intent(this, StudentRegistration::class.java)
        startActivity(intent)
        finish()
    }


    fun forgetPassword(view: View) {
        var intent:Intent= Intent(this,ForgetPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }
}


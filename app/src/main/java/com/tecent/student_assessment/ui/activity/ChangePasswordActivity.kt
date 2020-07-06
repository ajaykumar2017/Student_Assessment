package com.tecent.student_assessment.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.DataUtils
import kotlinx.android.synthetic.main.activity_change_password.btn_submit
import kotlinx.android.synthetic.main.activity_change_password.et_confirm_new_password
import kotlinx.android.synthetic.main.activity_change_password.et_confirm_old_password
import kotlinx.android.synthetic.main.activity_change_password.et_new_password
import kotlinx.android.synthetic.main.activity_change_password.et_old_password
import kotlinx.android.synthetic.main.activity_settings.toolbar_main
import org.json.JSONObject
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class ChangePasswordActivity : AppCompatActivity() {
    lateinit internal var sharedPreferences: SharedPreferences
    lateinit var dialog: ACProgressFlower
    lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_change_password
        )
        setSupportActionBar(findViewById(
            id.toolbar_main
        ))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            drawable.ic_001_back
        )
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.title = Html.fromHtml("<font color='#000'>Change Password</font>")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        requestQueue = Volley.newRequestQueue(this)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build()
        sharedPreferences = this.getSharedPreferences(
            DataUtils.sharedPreferencesId, Context.MODE_PRIVATE
        )
        val passw:String=sharedPreferences.getString("passw","")!!
        val userid:String=sharedPreferences.getString("userid","")!!
        btn_submit.setOnClickListener {
            if (et_old_password.text.toString().trim()==""||et_confirm_old_password.text.toString().trim()==""||
                    et_new_password.text.toString().trim()==""||et_confirm_new_password.text.toString().trim()==""){
                Toast.makeText(this, "Please fill all Mandatory Fields!", Toast.LENGTH_SHORT).show()
            }
            else if (et_old_password.text.toString().trim()!=passw){
                Toast.makeText(this, "Please Enter correct Password", Toast.LENGTH_SHORT).show()
            }else if (et_old_password.text.toString().trim()!=et_confirm_old_password.text.toString().trim()){
                Toast.makeText(this, "Old Passwords Mismatch!", Toast.LENGTH_SHORT).show()
            }else if (et_new_password.text.toString().trim().length<8){
                Toast.makeText(this, "New Password should be at least 8 characters long!", Toast.LENGTH_SHORT).show()
            }else if (et_new_password.text.toString().trim()!=et_confirm_new_password.text.toString().trim()){
                Toast.makeText(this, "New Passwords Mismatch!", Toast.LENGTH_SHORT).show()
            }else{
              if (DataUtils.isNetworkStatusAvailable(this)) {
                dialog.show()
                try {
                  val url = DataUtils.serverurl + "EditProfile.php"
                  val stringRequest =
                    object : StringRequest(Method.POST, url, Response.Listener { response ->
                      //                progressBar.setVisibility(View.GONE)
                      try {
                        val emp = JSONObject(response)
                        val result = emp.getString("result")
                        if (result == "successful") {
                          Toast.makeText(this, "Password Changed", Toast.LENGTH_SHORT)
                              .show()
                          val sharedPreferencesEdit = sharedPreferences.edit()
                          sharedPreferencesEdit.putString("passw", et_new_password.text.toString()
                              .trim()
                          )
                          sharedPreferencesEdit.apply()
                                    dialog.dismiss()
                                    finish()
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
                                MyData["passw"] = et_new_password.text.toString().trim()
                                return MyData
                            }
                        }
                        requestQueue.add(stringRequest)
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(this, "some error occured!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }else{
                    Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}

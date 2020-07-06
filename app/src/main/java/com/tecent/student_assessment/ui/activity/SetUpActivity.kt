package com.tecent.student_assessment.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.DataUtils
import org.json.JSONObject
import java.util.HashMap

@Suppress("DEPRECATION")
class SetUpActivity : AppCompatActivity() {
  lateinit internal var sharedPreferences: SharedPreferences
  lateinit internal var requestQueue: RequestQueue
  var progressDialog: ProgressDialog? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_set_up)
    requestQueue = Volley.newRequestQueue(this)
    progressDialog = ProgressDialog(this@SetUpActivity)
    progressDialog!!.setTitle("Please wait")
    progressDialog!!.setMessage("Preparing to download...")
    progressDialog!!.setCancelable(false)
    progressDialog!!.show()
    sharedPreferences =
      getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("email", "")!!
    val password = sharedPreferences.getString("passw", "")!!
    //volley part start
    val url = DataUtils.serverurl + "studentLoginData.php"
    val stringRequest: StringRequest = object : StringRequest(
        Method.POST, url,
        Listener { response ->
          try {
            progressDialog!!.dismiss()
            val emp = JSONObject(response)
            val spe = sharedPreferences.edit()
            spe.putString("userid", emp.getString("userid"))
            spe.putString("name", emp.getString("name"))
            spe.putString("gender", emp.getString("gender"))
            spe.putString("branch", emp.getString("branch"))
            spe.putString("semester", emp.getString("semester"))
            spe.putString("college", emp.getString("college"))
            spe.putString("university", emp.getString("university"))
            spe.putString("verified", emp.getString("verified"))
            spe.putString("ban", emp.getString("ban"))
            spe.putString("joindate", emp.getString("joindate"))
            spe.putString("userdp", emp.getString("userdp"))
            spe.putString("posts", emp.getString("posts"))
            spe.putString("doubts", emp.getString("doubts"))
            spe.putString("answers", emp.getString("answers"))
            spe.apply()
            val intentNew = Intent(this@SetUpActivity, HomeActivity::class.java)
            startActivity(intentNew)
            finish()
          } catch (exception: Exception) {
            progressDialog!!.dismiss()
          }
        }, ErrorListener {
      progressDialog!!.dismiss()
      Toast.makeText(this@SetUpActivity, "Error! Please try again later...", Toast.LENGTH_SHORT)
          .show()
    }) {
      override fun getParams(): Map<String, String> {
        val myData: MutableMap<String, String> =
          HashMap()
        myData["email"] = email
        myData["passw"] = password
        return myData
      }
    }
    requestQueue.add(stringRequest)
  }
}
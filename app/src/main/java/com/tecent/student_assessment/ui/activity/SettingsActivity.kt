package com.tecent.student_assessment.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.DataUtils
import kotlinx.android.synthetic.main.activity_settings.toolbar_main
import java.io.File
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_settings)
        setSupportActionBar(findViewById(
            id.toolbar_main
        ))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            drawable.ic_001_back
        )
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle(Html.fromHtml("<font color='#000'>Settings</font>"))
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        val switchnotification = findViewById<Switch>(
            id.switchnotification
        )
        val switchsound = findViewById<Switch>(
            id.switchsound
        )
        val btnchangepassword = findViewById<LinearLayout>(
            id.btnchangepassword
        )
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            DataUtils.sharedPreferencesId, Context.MODE_PRIVATE
        )
        val sharedPreferencesLike: SharedPreferences = getSharedPreferences(
            DataUtils.sharedPreferencesLikeId, Context.MODE_PRIVATE
        )
        val userid = sharedPreferences.getString("userid","")!!
        val requestQueue = Volley.newRequestQueue(this)
        val btnlogout = findViewById<LinearLayout>(
            id.btnlogout
        )
        //change password
        btnchangepassword.setOnClickListener {
           val intentPass=Intent(this,
               ChangePasswordActivity::class.java)
            startActivity(intentPass)
        }
        //logout start
        btnlogout.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        //volley part start
                      val url = DataUtils.serverurl + "userLogout.php"
                      val stringRequest = object : StringRequest(
                          Request.Method.POST, url, Response.Listener { response -> },
                          Response.ErrorListener {

                          }) {
                        override fun getParams(): Map<String, String> {
                          val MyData = HashMap<String, String>()
                          MyData["userid"] = userid
                          return MyData
                        }
                      }
                        requestQueue.add(stringRequest)
                        //volley part end

                      DataUtils.logOut(this)
                        val file = File(
                            DataUtils.rootdir + "userdp/" + sharedPreferences.getString(
                                "userdp", ""
                            )
                        )
                        if (file.exists()) {
                            file.delete()
                        }
                        finishAffinity()
                    }
                }
            }
            val builder = AlertDialog.Builder(this@SettingsActivity)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure want to Logout?")
            builder.setPositiveButton("Ok", dialogClickListener)
            builder.setNegativeButton("Cancel", dialogClickListener)
            builder.show()

        }

    }
}

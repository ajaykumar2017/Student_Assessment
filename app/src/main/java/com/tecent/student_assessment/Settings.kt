package com.tecent.student_assessment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File
import java.util.HashMap

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle(Html.fromHtml("<font color='#000'>Settings</font>"))

        val switchnotification = findViewById<Switch>(R.id.switchnotification)
        val switchsound = findViewById<Switch>(R.id.switchsound)
        val btnchangepassword = findViewById<LinearLayout>(R.id.btnchangepassword)
        val sharedPreferences: SharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        val userid:String=sharedPreferences.getString("userid","")
        val requestQueue = Volley.newRequestQueue(this)
        val btnlogout = findViewById<LinearLayout>(R.id.btnlogout)
        btnlogout.setOnClickListener {

            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        //volley part start
                        val url = ExtraFunctions.serverurl + "userLogout.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> }, Response.ErrorListener {

                        }) {
                            override fun getParams(): Map<String, String> {
                                val MyData = HashMap<String, String>()
                                MyData["userid"] = userid
                                return MyData
                            }
                        }
                        requestQueue.add(stringRequest)
                        //volley part end

                        val spe = sharedPreferences.edit()
                        spe.putString("userid", "")
                        spe.putString("name", "")
                        spe.putString("gender", "")
                        spe.putString("branch", "")
                        spe.putString("semester", "")
                        spe.putString("college", "")
                        spe.putString("university", "")
                        spe.putString("verified", "")
                        spe.putString("ban", "")
                        spe.putString("joindate", "")
                        spe.putString("userdp", "")
                        spe.putString("email", "")
                        spe.putString("passw", "")
                        spe.putString("points", "")
                        spe.putString("login", "false")
                        spe.commit()
                        val file = File(ExtraFunctions.rootdir + "userdp/" + sharedPreferences.getString("userdp", ""))
                        if (file.exists()) {
                            file.delete()
                        }
                        val intent = Intent(this, StudentLoginActivity::class.java)
                        startActivity(intent)

                    }
                }
            }
            val builder = AlertDialog.Builder(this@Settings)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure want to Logout?")
            builder.setPositiveButton("Ok", dialogClickListener)
            builder.setNegativeButton("Cancel", dialogClickListener)
            builder.show()

        }
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

    }
}

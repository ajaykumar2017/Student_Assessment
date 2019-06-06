package com.tecent.student_assessment

import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*

class ContactUs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle(Html.fromHtml("<font color='#000'>Contact Us</font>"))
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
    }
}

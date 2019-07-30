package com.tecent.student_assessment

import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_dash_board_menu_web_view.*
import kotlinx.android.synthetic.main.toolbar_main.*

class DiscussionForum : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion_forum)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Discussion Forum")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

    }
}

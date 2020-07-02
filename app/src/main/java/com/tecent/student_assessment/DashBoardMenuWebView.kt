package com.tecent.student_assessment

import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tecent.student_assessment.extraFunctions.ExtraFunctions
import kotlinx.android.synthetic.main.activity_dash_board_menu_web_view.*
import kotlinx.android.synthetic.main.toolbar_main.*

class DashBoardMenuWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_menu_web_view)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        dashboard_webview.getSettings().setJavaScriptEnabled(true)
        dashboard_webview.getSettings().setBuiltInZoomControls(false)
        val intentTitle = intent
        val title: String = intentTitle.getStringExtra("title")
        supportActionBar!!.setTitle(title.trim())
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        dashboard_webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar_dashboard.visibility = View.GONE
            }
        }
        when (title) {
            "Prepare" -> dashboard_webview.loadUrl(
                ExtraFunctions.serverurl + "dashboard/dashboardPrepare.php")
            "Preparation Materials" -> dashboard_webview.loadUrl(
                ExtraFunctions.serverurl + "dashboard/dashboardPrepareMaterials.php")
            "Quizes" -> dashboard_webview.loadUrl(
                ExtraFunctions.serverurl + "dashboard/dashboardQuizes.php")
            "Tutorials" -> dashboard_webview.loadUrl("https://www.tutorials.a3creators.co.in")
            "Useful Links" -> dashboard_webview.loadUrl(
                ExtraFunctions.serverurl + "dashboard/usefulLinksDashboard.php")
            "Practice" -> dashboard_webview.loadUrl(
                ExtraFunctions.serverurl + "dashboard/Practice.php")
        }

    }
}

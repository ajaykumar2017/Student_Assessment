package com.tecent.student_assessment.ui.activity

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.ExtraFunctions.serverurl
import kotlinx.android.synthetic.main.activity_dash_board_menu_web_view.dashboard_webview
import kotlinx.android.synthetic.main.activity_dash_board_menu_web_view.progress_bar_dashboard
import kotlinx.android.synthetic.main.toolbar_main.toolbar_main

class DashBoardMenuWebViewActivity : AppCompatActivity() {

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        layout.activity_dash_board_menu_web_view
    )
    setSupportActionBar(
        findViewById(
            id.toolbar_main
        )
    )
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.setHomeAsUpIndicator(
        drawable.ic_001_back
    )
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
    dashboard_webview.settings.javaScriptEnabled = true
    dashboard_webview.settings.builtInZoomControls = false
    val intentTitle = intent
    val title: String = intentTitle.getStringExtra("title")
    supportActionBar!!.title = title.trim()
    toolbar_main.setNavigationOnClickListener {
      finish()
    }
    dashboard_webview.webViewClient = object : WebViewClient() {
      override fun onPageFinished(
        view: WebView?,
        url: String?
      ) {
        super.onPageFinished(view, url)
        progress_bar_dashboard.visibility = View.GONE
      }
    }
    when (title) {
      "Prepare" -> dashboard_webview.loadUrl(
          serverurl + "dashboard/dashboardPrepare.php"
      )
      "Preparation Materials" -> dashboard_webview.loadUrl(
          serverurl + "dashboard/dashboardPrepareMaterials.php"
      )
      "Quizes" -> dashboard_webview.loadUrl(
          serverurl + "dashboard/dashboardQuizes.php"
      )
      "Tutorials" -> dashboard_webview.loadUrl("https://www.tutorials.a3creators.co.in")
      "Useful Links" -> dashboard_webview.loadUrl(
          serverurl + "dashboard/usefulLinksDashboard.php"
      )
      "Practice" -> dashboard_webview.loadUrl(
          serverurl + "dashboard/Practice.php"
      )
    }

  }

  override fun onBackPressed() {
    if (dashboard_webview.canGoBack()) {
      dashboard_webview.goBack()
    } else {
      super.onBackPressed()
    }
  }
}

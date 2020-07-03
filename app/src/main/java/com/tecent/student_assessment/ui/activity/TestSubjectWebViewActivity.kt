package com.tecent.student_assessment.ui.activity

import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_image_pdf_web_view.progress_bar
import kotlinx.android.synthetic.main.activity_test_subject_web_view.webView
import kotlinx.android.synthetic.main.toolbar_main.toolbar_main
import java.util.Locale

class TestSubjectWebViewActivity : AppCompatActivity() {

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        layout.activity_test_subject_web_view
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
    webView.settings.javaScriptEnabled = true
    webView.settings.builtInZoomControls = false
    val intentSub = intent
    val testType: String = intent.getStringExtra("testType")
    val testSubject: String = intent.getStringExtra("testSubject")
    val testSubjectShort: String = intent.getStringExtra("testSubjectShort")
    val type: String = intent.getStringExtra("test")
    val sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
    val userid = sharedPreferences.getString("userid", "")
    supportActionBar!!.title = testSubjectShort.trim()
        .toUpperCase(Locale.getDefault()) + " " + testType.trim()
    toolbar_main.setNavigationOnClickListener {
      finish()
    }

    webView.webViewClient = object : WebViewClient() {
      override fun onPageFinished(
        view: WebView?,
        url: String?
      ) {
        super.onPageFinished(view, url)
        progress_bar.visibility = View.GONE
      }
    }
    if (type == "Practice") {
      when (testSubjectShort) {
        "algo" -> webView.loadUrl(
            serverurl + "testSeries/" + "algorithmPracticeSet.php?userid=" + userid
        )
        "ds" -> webView.loadUrl(
            serverurl + "testSeries/" + "dataStructurePracticeSet.php?userid=" + userid
        )
        "cd" -> webView.loadUrl(
            serverurl + "testSeries/" + "CompilerDesignPracticeSet.php?userid=" + userid
        )
        "toc" -> webView.loadUrl(
            serverurl + "testSeries/" + "TOCPracticeSet.php?userid=" + userid
        )
        "dbms" -> webView.loadUrl(
            serverurl + "testSeries/" + "DBMSPracticeSet.php?userid=" + userid
        )
        "os" -> webView.loadUrl(
            serverurl + "testSeries/" + "OSPracticeSet.php?userid=" + userid
        )
        "dl" -> webView.loadUrl(
            serverurl + "testSeries/" + "digitalLogicPracticeSet.php?userid=" + userid
        )
        "co" -> webView.loadUrl(
            serverurl + "testSeries/" + "COPracticeSet.php?userid=" + userid
        )
        "cn" -> webView.loadUrl(
            serverurl + "testSeries/" + "ComputerNetworkPracticeSet.php?userid=" + userid
        )
        "dm" -> webView.loadUrl(
            serverurl + "testSeries/" + "DiscreteMathPracticeSet.php?userid=" + userid
        )
        "c" -> webView.loadUrl(
            serverurl + "testSeries/" + "CLanguagePracticeSet.php?userid=" + userid
        )
        "cpp" -> webView.loadUrl(
            serverurl + "testSeries/" + "CppPracticeSet.php?userid=" + userid
        )
        "java" -> webView.loadUrl(
            serverurl + "testSeries/" + "javaPracticeSet.php?userid=" + userid
        )
        "python" -> webView.loadUrl(
            serverurl + "testSeries/" + "pythonPracticeSet.php?userid=" + userid
        )
        "android" -> webView.loadUrl(
            serverurl + "testSeries/" + "AndroidPracticeSet.php?userid=" + userid
        )
        else -> webView.loadUrl(
            serverurl + "testSeries/" + "algorithmPracticeSet.php?userid=" + userid
        )
      }

    } else {
      webView.loadUrl(
          serverurl + "testSeries/" + "algorithmTestSeries.php?userid=" + userid
      )
    }

  }

  override fun onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack()
    } else {
      super.onBackPressed()
    }
  }
}

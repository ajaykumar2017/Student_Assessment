package com.tecent.student_assessment

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_image_pdf_web_view.*
import kotlinx.android.synthetic.main.toolbar_main.*

class TestSubjectWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_subject_web_view)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setBuiltInZoomControls(false)
        val intentSub = intent
        val testType:String=intent.getStringExtra("testType")
        val testSubject:String=intent.getStringExtra("testSubject")
        val testSubjectShort:String=intent.getStringExtra("testSubjectShort")
        val type:String=intent.getStringExtra("test")
        val sharedPreferences= getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)
        val userid:String=sharedPreferences.getString("userid","")
        supportActionBar!!.setTitle(testSubjectShort.trim().toUpperCase()+" "+testType.trim())
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        webView.webViewClient=object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar.visibility= View.GONE
            }
        }
        if (type=="Practice"){
            if (testSubjectShort=="algo"){
                webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"algorithmPracticeSet.php?userid="+userid)
            }else if (testSubjectShort=="ds"){
                webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"dataStructurePracticeSet.php?userid="+userid)
            }else if (testSubjectShort=="dl"){
                webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"digitalLogicPracticeSet.php?userid="+userid)
            }else if (testSubjectShort=="c"){
                webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"CLanguagePracticeSet.php?userid="+userid)
            }else{
                webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"algorithmPracticeSet.php?userid="+userid)
            }

        }else{
            webView.loadUrl(ExtraFunctions.serverurl+"testSeries/"+"algorithmTestSeries.php?userid="+userid)
        }


    }
}

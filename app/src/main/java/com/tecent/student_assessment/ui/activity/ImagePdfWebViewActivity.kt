package com.tecent.student_assessment.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tecent.student_assessment.R.layout
import kotlinx.android.synthetic.main.activity_image_pdf_web_view.*

class ImagePdfWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_image_pdf_web_view
        )
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setBuiltInZoomControls(true)
        webView.fitsSystemWindows=true
        val viewLink = intent.getStringExtra("viewLink")
        webView.webViewClient=object :WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar.visibility=View.GONE
            }
        }
        webView.loadUrl(viewLink)
    }
}

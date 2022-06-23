package se.eoslund.piggest.services

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class MyWebViewClient(private val progressBar: ProgressBar): WebViewClient(){
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE
    }
}
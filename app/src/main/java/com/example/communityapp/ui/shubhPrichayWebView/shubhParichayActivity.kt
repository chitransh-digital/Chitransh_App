package com.example.communityapp.ui.shubhPrichayWebView

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityShubhParichayBinding

class shubhParichayActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShubhParichayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShubhParichayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get a reference to the WebView
        val myWebView: WebView = findViewById(R.id.webview)

        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        myWebView.settings.javaScriptEnabled = true

        myWebView.loadUrl("https://www.shubhparichay.in/")
    }
}
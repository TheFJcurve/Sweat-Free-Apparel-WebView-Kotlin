package com.example.sfawebview

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var toolbar: Toolbar
    private val homeUrl = "https://www.sweatfree.co"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        webView.webViewClient = WebViewClient()
        webView.loadUrl(homeUrl)
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_back -> {
                onBackPressed()
                true
            }

            R.id.action_home -> {
                navigateHome()
                true
            }

            R.id.action_copy_link -> {
                copyCurrentLink()
                true
            }

            R.id.action_go_forward -> {
                goForward()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun copyCurrentLink() {
        val currentUrl = webView.url
        if (currentUrl != null) {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("URL", currentUrl)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "URL Copied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Unable to get current URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goForward() {
        if (webView.canGoForward()) {
            webView.goForward()
        } else {
            Toast.makeText(this, "Cannot go forward", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateHome() {
        webView.loadUrl(homeUrl)
    }
}

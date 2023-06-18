package com.example.sfawebview

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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

            R.id.action_go_forward -> {
                goForward()
                true
            }

            R.id.action_share -> {
                shareLink()
                true
            }
            R.id.action_open_external -> {
                openExternalLink()
                true
            }
            R.id.action_refresh -> {
                refreshPage()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
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

    private fun shareLink() {
        val currentUrl = webView.url
        if (currentUrl != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentUrl)
            startActivity(Intent.createChooser(shareIntent, "Share Link"))
        } else {
            Toast.makeText(this, "Unable to get current URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openExternalLink() {
        val currentUrl = webView.url
        if (currentUrl != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to get current URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshPage() {
        webView.reload()
    }

}

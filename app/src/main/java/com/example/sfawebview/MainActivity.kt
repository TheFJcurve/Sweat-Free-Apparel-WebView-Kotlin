package com.example.sfawebview

// This is The MainActivity file of the project. Here all the compilation, JavaScript coding
// and button functionality is written.

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging


// In this document you will see Deprecation and Deprecated In Java Terms thrown around.
// They do not mean any error or harm. These are simply those modules or methods that are
// a little older than the main stream, and are not preferred among developers.

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var toolbar: Toolbar
//    private lateinit var appBarLayout: AppBarLayout
    private val homeUrl = "https://www.sweatfree.co" // Our main Website URL

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        // Register for Firebase Cloud Messaging
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get the token
            val token = task.result

            // TODO: Send the token to your server or handle it as required
            Log.d(TAG, "FCM registration token: $token")
        })


        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        toolbar = findViewById(R.id.toolbar)


        // When you want the toolbar to only open when you open an external website

//        webView.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                if (url != null && (!url.startsWith(homeUrl) || url == homeUrl)) {
//                    supportActionBar?.show() // Show the toolbar for external websites
//                } else {
//                    supportActionBar?.hide() // Hide the toolbar for the home page
//                }
//            }
//        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setting up the WebView. This shows our website in the app. Below I have made it possible
        // to zoom and write JavaScript code (to make buttons and write other commands).
        // If you wish to change the website displayed, just change the homeUrl variable, or
        // input the url you wish to see in .loadUrl()
        webView.loadUrl(homeUrl)
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true

        // Setting up a dynamic toolbar, which hides when we are scrolling through the app.
        // It also automatically comes back up when needed.
        // appBarLayout = findViewById(R.id.appBarLayout)

//        webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//            if (scrollY < oldScrollY) {
//                appBarLayout.visibility = View.GONE // Hide the AppBarLayout
//            } else {
//                appBarLayout.visibility = View.VISIBLE // Show the AppBarLayout
//            }
//        }

        // Checking if Firebase works
        FirebaseApp.initializeApp(this)

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    // menu_main.xml contains all the information regarding a button (the button icon, positioning,
    // etc). Calling this function helps us get the menu_main information into this project.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Setting up all of the functions

    // When a button is clicked, this function calls the designated button function on it. Here
    // we link a button with the function.
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

            R.id.action_refresh -> {
                refreshPage()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Setting up button functions

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Go to the previously shown page each time you click, until can't go back.
        // Then display a text saying "cannot go back"
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            Toast.makeText(this, "Cannot go Back", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goForward() {
        // Go to the forward each time you click, until you can't.
        // Then display a text saying "cannot go forward"
        if (webView.canGoForward()) {
            webView.goForward()
        } else {
            Toast.makeText(this, "Cannot go forward", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateHome() {
        // Calls the home url and loads that page. Therefore, this takes us back to the main page
        webView.loadUrl(homeUrl)

    }

    private fun shareLink() {
        // Creates a popup window that helps in sharing the link or article. Copying, and opening
        // in browser is inbuilt.
        val currentUrl = webView.url
        if (currentUrl != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentUrl)

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))

            // Create a chooser with both the share intent and the browser intent
            val chooserIntent = Intent.createChooser(shareIntent, "Share Link")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(browserIntent))

            startActivity(chooserIntent)
        } else {
            Toast.makeText(
                this, "Unable to get current URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshPage() {
        // Reloads page
        webView.reload()
    }
}

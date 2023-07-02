package com.example.sfawebview

// This is The MainActivity file of the project. Here all the compilation, JavaScript coding
// and button functionality is written.

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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


        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && (!url.startsWith(homeUrl) || url == homeUrl)) {
                    supportActionBar?.show() // Show the toolbar for external websites
                } else {
                    supportActionBar?.hide() // Hide the toolbar for the home page
                }
            }
        }

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

        // Check if the activity was opened from a notification
        val intent = intent
        if (intent.extras != null) {
            // Handle the notification data here
            val notificationData = intent.extras
            // TODO: Handle the notification data as required
        }

    }

    // menu_main.xml contains all the information regarding a button (the button icon, positioning,
    // etc). Calling this function helps us get the menu_main information into this project.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Setting up all of the functions

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: Display an educational UI explaining to the user the features that will be enabled
                // by granting the POST_NOTIFICATION permission. This UI should provide the user with
                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                // If the user selects "No thanks," allow the user to continue without notifications.

                // Create and show an AlertDialog with the educational UI
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Notification Permission")
                    .setMessage("Granting the notification permission allows you to receive important updates and notifications.")
                    .setPositiveButton("OK") { dialog, _ ->
                        // Request the permission when the user selects "OK"
                        requestNotificationPermission()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No thanks") { dialog, _ ->
                        // Handle "No thanks" action, allow the user to continue without notifications
                        dialog.dismiss()
                    }

                val dialog = dialogBuilder.create()
                dialog.show()
            } else {
                // Directly ask for the permission
                requestNotificationPermission()
            }
        }
    }

    private fun requestNotificationPermission() {
        // Request the POST_NOTIFICATIONS permission using the permission launcher
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


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

package com.tugasakhirrifgi.lsdetect.ui.home.kasuslsd


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.tugasakhirrifgi.lsdetect.databinding.ActivityWebLsdBinding


class WebLsdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebLsdBinding

    private val URL = "https://validation.isikhnas.com/?_token=FzixjkNd9IC3pHSGEuTgn6lpxYukkyOLDLo1QWz4&year=2023&priority=83"
    private var isAlreadyCreated = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebLsdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoaderAnimation()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportZoom(true)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                endLoaderAnimate()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                endLoaderAnimate()
                showErrorDialog("Error", "No internet Connection. Please check your connection", this@WebLsdActivity)
            }
        }
        binding.webView.loadUrl(URL)

        supportActionBar?.title = "Informasi Kasus LSD"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (isAlreadyCreated && !isNetworkAvailable()) {
            isAlreadyCreated = false
            showErrorDialog("Error", "No internet Connection. Please check your connection", this@WebLsdActivity)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectionManager = this@WebLsdActivity.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)

    }

    private fun showErrorDialog(title: String, message: String, context: android.content.Context) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("cancel") { _, _ ->
            this@WebLsdActivity.finish()
        }

        dialog.setNeutralButton("Settings") {_, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }

        dialog.setPositiveButton("Retry") {_, _ ->
            this@WebLsdActivity.recreate()
        }
        dialog.create().show()
    }

    private fun endLoaderAnimate() {
        binding.loaderImage.clearAnimation()
        binding.loaderImage.visibility = View.GONE
    }

    private fun startLoaderAnimation(){
        val startHeight = 170
        val endHeight = startHeight + 40

        val valueAnimator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {animation ->
                val newHeight = animation.animatedValue as Int
                val layoutParams = binding.loaderImage.layoutParams
                layoutParams.height = newHeight
                binding.loaderImage.layoutParams = layoutParams
            }
        }

        valueAnimator.start()
    }

}
package com.tugasakhirrifgi.lsdetect.ui.home.tentang

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tentang Aplikasi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val htmlContent = getString(R.string.descFitur)
        val htmlContent1 = getString(R.string.descKontak)

        binding.apply {

            tvDescFeature.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(htmlContent)
            }

            tvDescKontak.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlContent1, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(htmlContent1)
            }

        }

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

}
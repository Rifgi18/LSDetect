package com.tugasakhirrifgi.lsdetect.ui.newHistory.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.tugasakhirrifgi.lsdetect.databinding.ActivityDetailHistoryBinding
import com.tugasakhirrifgi.lsdetect.ui.newHistory.NewHistoryDetection

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private var historyData: NewHistoryDetection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hideSystemUI()

        historyData = intent.getParcelableExtra(EXTRA_DATA)

        binding.apply {

            Glide.with(this@DetailHistoryActivity)
                .load(historyData?.image)
                .into(imgvContent)

            tvHasilText.text = historyData?.hasil
            tvConfidence.text = historyData?.confidence
            tvDateContent.text = historyData?.timestamp
            tvTreatContent.text = historyData?.tindakan
        }

        supportActionBar?.title = historyData?.hasil
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    companion object{
        const val EXTRA_DATA = "data"
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

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

}
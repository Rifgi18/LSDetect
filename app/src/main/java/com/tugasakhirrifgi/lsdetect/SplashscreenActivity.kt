package com.tugasakhirrifgi.lsdetect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tugasakhirrifgi.lsdetect.databinding.ActivitySplashscreenBinding

class SplashscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
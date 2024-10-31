package com.tugasakhirrifgi.lsdetect.ui.home.inform1

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.ActivityInformGejalaBinding

class InformGejalaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformGejalaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformGejalaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Informasi Gejala"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val arrow : ImageView = findViewById(R.id.iv_back_arrow)
        val tvAppbar : TextView = findViewById(R.id.tv_name_act)
        val htmlContent = getString(R.string.htmlText1)


        binding.apply {

            tvKonten1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(htmlContent)
            }

        }


        arrow.setOnClickListener {
            finish()
        }
    }

}
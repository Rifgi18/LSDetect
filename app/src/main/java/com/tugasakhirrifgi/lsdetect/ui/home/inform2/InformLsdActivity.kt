package com.tugasakhirrifgi.lsdetect.ui.home.inform2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StringRes
import com.tugasakhirrifgi.lsdetect.R
import com.tugasakhirrifgi.lsdetect.databinding.ActivityInformLsdBinding
import com.tugasakhirrifgi.lsdetect.ui.home.inform2.adapter.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class InformLsdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformLsdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformLsdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionPager = SectionsPagerAdapter(this)
        val viewpager = binding.viewPager
        viewpager.adapter = sectionPager
        val tabs = binding.tabs
        TabLayoutMediator(tabs, viewpager) {tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


        supportActionBar?.title = "Informasi LSD"
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

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3
        )
    }

}
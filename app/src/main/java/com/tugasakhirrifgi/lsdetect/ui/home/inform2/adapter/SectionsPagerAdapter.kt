package com.tugasakhirrifgi.lsdetect.ui.home.inform2.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tugasakhirrifgi.lsdetect.ui.home.inform2.InformationFragment
import com.tugasakhirrifgi.lsdetect.ui.home.inform2.MedicineFragment
import com.tugasakhirrifgi.lsdetect.ui.home.inform2.TindakanFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = InformationFragment()
            1 -> fragment = TindakanFragment()
            2 -> fragment = MedicineFragment()
        }
        return fragment as Fragment
    }
}
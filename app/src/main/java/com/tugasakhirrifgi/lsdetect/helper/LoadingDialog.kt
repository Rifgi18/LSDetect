package com.tugasakhirrifgi.lsdetect.helper

import android.app.Activity
import android.app.AlertDialog
import com.tugasakhirrifgi.lsdetect.R

class LoadingDialog(val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog

    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)


        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}
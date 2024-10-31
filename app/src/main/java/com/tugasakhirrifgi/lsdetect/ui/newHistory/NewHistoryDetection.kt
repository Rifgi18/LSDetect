package com.tugasakhirrifgi.lsdetect.ui.newHistory

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewHistoryDetection(
    var confidence: String? = null,
    var hasil: String? = null,
    var image: String? = null,
    var timestamp: String? = null,
    var tindakan: String? = null,
) : Parcelable

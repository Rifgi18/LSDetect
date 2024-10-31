package com.tugasakhirrifgi.lsdetect.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    val userData = MutableLiveData<UserData>()
    //val userProfile: LiveData<UserData> get() = userData

    fun updateUserData(newData: UserData){
        userData.value = newData
    }

}
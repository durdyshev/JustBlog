package com.example.justblog.main.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.google.firebase.auth.FirebaseUser

class MainActivityViewModel(application: Application): BaseViewModel(application) {
    val currentUserValue= MutableLiveData<FirebaseUser?>()
    val text=MutableLiveData<String>()

    fun getFirebaseAuth(){
        text.value="haha"
    }
}
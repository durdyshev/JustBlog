package com.example.justblog.login.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginViewModel (application: Application): BaseViewModel(application) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val loginBoolean= MutableLiveData<Boolean>()
    val loginString= MutableLiveData<String>()

    fun loginUser(email: String, password: String,) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                loginBoolean.value=true
            }
            else{
                loginBoolean.value=false
                loginString.value=it.exception.toString()
            }
        }
    }
}
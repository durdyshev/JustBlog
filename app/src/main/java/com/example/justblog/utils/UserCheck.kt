package com.example.justblog.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

class UserCheck(val context:Context) {
    private lateinit var firebaseAuth:FirebaseAuth

    private val userSharedPreferences: SharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

    fun userId(): String? {
        return if(userSharedPreferences.contains("userId")){
            userSharedPreferences.getString("userId","")
        } else{
            firebaseAuth=FirebaseAuth.getInstance()
            firebaseAuth.currentUser!!.uid
        }
    }
}
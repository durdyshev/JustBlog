package com.example.justblog.utils

import android.content.Context
import android.content.SharedPreferences

class UserCheck(val context:Context) {


    private val userSharedPreferences: SharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

    fun userId(): String? {
        return userSharedPreferences.getString("userId","")
    }
}
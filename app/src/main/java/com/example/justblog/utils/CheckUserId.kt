package com.example.justblog.utils

object CheckUserId {
    fun checkUserId(usersId: ArrayList<String>, userId: String?): String? {
        usersId.forEach {
            if (it != userId) {
                return it
            }
        }
        return null
    }
}
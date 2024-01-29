package com.example.justblog.main.model

import java.util.Date

data class CommentData(
    val commentId: String? = "",
    val comment: String? = "",
    val date: Date? = null,
    val userId:String=""
)

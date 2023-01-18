package com.example.justblog.main.model

import java.util.Date

class PostData(
    val postId:String,
    val comp_url:String?="",
    val description:String?="",
    val image_url:String?="",
    val type:String?="",
    val user_id:String?="",
    val date: Date? =null )
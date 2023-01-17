package com.example.justblog.main.model

class PostData(
    val postId:String,
    val comp_url:String?="",
    val description:String?="",
    val image_url:String?="",
    val type:String?="",
    val user_id:String?="",
    val date: com.google.firebase.Timestamp? =null )
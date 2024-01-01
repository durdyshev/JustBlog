package com.example.justblog.main.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileData(
    @PropertyName("name")
    val name:String?=null,
    val username:String?=null,
    val motto:String?=null,
    @PropertyName("profile_img")
    val profileImg:String?=null,
    val userId:String?=null
) : Parcelable

class ProfileSelectData(
    val name:String
)
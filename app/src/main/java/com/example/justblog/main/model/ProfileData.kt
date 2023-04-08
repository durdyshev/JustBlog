package com.example.justblog.main.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileData(
    val name:String?=null,
    val username:String?=null,
    val motto:String?=null,
    val profileImg:String?=null,
    val userId:String?=null
) : Parcelable

class ProfileSelectData(
    val name:String
)
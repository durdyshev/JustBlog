package com.example.justblog.data.state

import com.example.justblog.main.model.ProfileData

data class ProfileState(
    val success: Boolean = false,
    val error: String = "",
    val profileData: ProfileData? = null,
    val isLoading: Boolean = false, )
package com.ptithcm.core.model


import com.google.gson.annotations.SerializedName

data class Info(
    val address: String,
    val addressDetail: String,
    val description: String,
    val email: String,
    val id: Int,
    val phoneNumber: String,
    val versionApp: String
)
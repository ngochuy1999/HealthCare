package com.ptithcm.core.model


import com.google.gson.annotations.SerializedName

data class Notification(
    val contentNoti: String,
    val dateNoti: String,
    val idNoti: Int,
    val isAdmin: Int
)
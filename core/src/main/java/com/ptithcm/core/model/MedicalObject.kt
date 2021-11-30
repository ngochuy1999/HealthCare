package com.ptithcm.core.model


import com.google.gson.annotations.SerializedName

data class MedicalObject(
    val active: Int,
    val description: String,
    val id: Int,
    val name: String
)
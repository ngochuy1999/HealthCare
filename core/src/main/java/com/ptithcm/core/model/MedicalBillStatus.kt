package com.ptithcm.core.model


import com.google.gson.annotations.SerializedName

data class MedicalBillStatus(
    val description: String,
    val statusId: Int,
    val statusName: String
)
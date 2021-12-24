package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalBillStatus(
    val description: String,
    val statusId: Int,
    val statusName: String
): Parcelable
package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalRecord(
    val active: Int? = null,
    val diagnostic: String? = "",
    val drugAllergy: String? = "",
    val recordId: Int? = null,
    val medicalBill: MedicalBill? = null
):Parcelable
package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clinic(
    val active: Int?= null,
    val clinicId: Int?= null,
    val clinicName: String?= "",
    val note: String?= ""
):Parcelable
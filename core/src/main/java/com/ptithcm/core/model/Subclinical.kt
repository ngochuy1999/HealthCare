package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subclinical(
    val price: Int,
    val subclinicalId: Int,
    val testName: String
):Parcelable
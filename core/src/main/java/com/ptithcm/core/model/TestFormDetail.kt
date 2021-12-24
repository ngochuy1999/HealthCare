package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TestFormDetail(
    val assignTime: String,
    val note: String,
    val subclinical: Subclinical
):Parcelable
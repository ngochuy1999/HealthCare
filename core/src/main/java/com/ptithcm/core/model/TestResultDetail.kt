package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TestResultDetail(
    val active: Int? = null,
    val fileUrl: String? = "",
    val id: Int? = null,
    val imageUrl: String? =""
):Parcelable
package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalObject(
    val active: Int,
    val description: String,
    val id: Int,
    val name: String
): Parcelable
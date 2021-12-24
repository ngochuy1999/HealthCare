package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Prescription(
    val active: Int? = null,
    val medicine: Medicine? = null,
    val id: Id? = null,
    val quantity: Int? = null
): Parcelable
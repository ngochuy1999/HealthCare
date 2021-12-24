package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Id(
    val medicineId: Int? = null,
    val treatmentId: Int? = null
): Parcelable
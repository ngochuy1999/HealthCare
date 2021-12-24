package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsultingRoom(
    val active: Int,
    val clinicId: Int,
    val clinicName: String,
    val note: String
): Parcelable
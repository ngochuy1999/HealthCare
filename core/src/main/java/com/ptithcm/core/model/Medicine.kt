package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Medicine(
    val dosage: String? = "",
    val medicineId: Int? = null,
    val medicineName: String? ="",
    val uses: String? = ""
):Parcelable
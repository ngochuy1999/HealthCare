package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TreatmentRegiment(
    val dateBegin: String? = "",
    val dateEnd: String? = "",
    val needs: String? = "",
    val prescriptions: ArrayList<Prescription>? = null,
    val prohibited: String? = "",
    val reExaminationDate: String? = "",
    val treatmentId: Int? = null
): Parcelable
package com.ptithcm.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.ptithcm.core.data.remote.DynamicSearchAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MedicalBill(
    val billId: Int? = null,
    @SerializedName(value = "dateCreate", alternate = ["date"])
    val date: String,
    val timePrediction: String,
    val examinationFee: Float,
    val medicalBillStatus: MedicalBillStatus,
    val medicalObject: MedicalObject,
    val patient: Profile,
    val waitingTime: String,
    val doctor: Doctor
): DynamicSearchAdapter.Searchable, Parcelable {
    override fun getSearchCriteria(): String = doctor.firstName?: ""
    override fun toString() = doctor.firstName ?: ""
}
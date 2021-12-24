package com.ptithcm.core.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TestResult(
    val active: Int? = null,
    val conclude: String? = "",
    val date: String? = "",
    val resultId: Int? = null,
    val testForm: TestForm? = null,
    val doctor: Doctor? = null
):Parcelable
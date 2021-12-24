package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TestForm (
    val diagnostic: String? = "",
    val id: Int? = null,
    val isPay: Int? = null,
    val medicalBill: MedicalBill? = null,
    var testFormDetail: ArrayList<TestFormDetail>
): Parcelable
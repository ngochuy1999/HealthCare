package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Role(
    val description: String,
    val roleId: Int,
    val roleName: String
): Parcelable
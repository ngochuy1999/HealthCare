package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val accountId: Int,
    val active: Int? = null,
    val email: String?,
    val isAccuracy: Int? = null,
    val password: String?,
    val phone : String?,
    val name: String?,
    val role: Role? = null,
    val userName: String?,
    var cover : String? = "",
    var avatar: String? = ""
): Parcelable
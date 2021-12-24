package com.ptithcm.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    var account: Account? = null,
    var active: Int? =null,
    var address: String? ="",
    var birthday: String?,
    var gender: Int? = null,
    var identityCard: String?= "",
    var imageUrl: String?="",
    var name: String?= "",
    var userId: Int? =null
): Parcelable
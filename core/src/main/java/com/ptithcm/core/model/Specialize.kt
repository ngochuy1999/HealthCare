package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Specialize(
    var specialityId: Int? = null,
    var name: String? = "",
    var description: String? = null,
    var active: Int? = null,
    var imageUrl: String? = null
) : Parcelable
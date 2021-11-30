package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doctor(
    var doctorId: Int? = null,
    var firstName: String? = "",
    var lastName: String? = "",
    var description: String? = "",
    var active: Int? = null,
    var isLike: Int? = null,
    var yearExperience: String? = "",
    var speciality: Specialize? = null,
    var timeAdvise: Int? =null,
    var address: String? = "",
    var imageUrl: String? = ""
): Parcelable {
    fun getIsFavorite() = isLike == 1
}
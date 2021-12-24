package com.ptithcm.core.model

import android.os.Parcelable
import com.ptithcm.core.data.remote.DynamicSearchAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doctor(
    var doctorId: Int? = null,
    var firstName: String? = "",
    var lastName: String? = "",
    var description: String? = "",
    var active: Int? = null,
    var yearExperience: String? = "",
    var speciality: Specialize? = null,
    var timeAdvise: Int? =null,
    var address: String? = "",
    var imageUrl: String? = "",
    var consultingRoom: ConsultingRoom? =null
): DynamicSearchAdapter.Searchable, Parcelable {
    override fun getSearchCriteria(): String = firstName?: ""
    override fun toString() = firstName ?: ""
}
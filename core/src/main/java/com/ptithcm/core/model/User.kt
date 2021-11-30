package com.ptithcm.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var billing_address_country: String? = "",
    var billing_address_county_area: String? = "",
    var billing_address_line_1: String? = "",
    var billing_address_line_2: String? = "",
    var billing_address_postcode_zip: String? = "",
    var billing_address_town_city: String? = "",
    var brand: Brand? = null,
    var cover: String? = "",
    var email: String? = "",
    var first_name: String? = "",
    var id: Int? = 0,
    var last_name: String? = "",
    var phone: String? = "",
    var photo: String? = "",
    var shipping_address_country: String? = "",
    var shipping_address_county_area: String? = "",
    var shipping_address_line_1: String? = "",
    var shipping_address_line_2: String? = "",
    var shipping_address_postcode_zip: String? = "",
    var shipping_address_town_city: String? = "",
    var shipping_telephone: String? = ""
) : Parcelable
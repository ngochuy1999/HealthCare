package com.ptithcm.core.model

import com.google.gson.annotations.SerializedName

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
)
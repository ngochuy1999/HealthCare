package com.ptithcm.core.param

data class EditAccountParam(
    var userid: Int?,
    var name: String?,
    var phone: String?,
    var identityCard: String?,
    var gender: Int? = null,
    var birthday: String?,
    var address: String?,
)
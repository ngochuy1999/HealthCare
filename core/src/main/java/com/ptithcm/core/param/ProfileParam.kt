package com.ptithcm.core.param

data class ProfileParam(
    val pid: Int? = null,
    val active: Int? = null,
    val identityCard: String?,
    val cover: String?,
    val avatar: String?,
    val name: String?,
    val gender: Int? = null,
    val birthday: String?,
    val address: String?,
)
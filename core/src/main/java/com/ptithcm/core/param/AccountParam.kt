package com.ptithcm.core.param

data class AccountParam(
    val active: Int? = null,
    val email: String?,
    val isAccuracy: Int? = null,
    val password: String?,
    val phone : String?,
    val userName: String?,
    val token: String?
)
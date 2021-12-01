package com.ptithcm.core.repository

import androidx.lifecycle.LiveData
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AccountParam
import com.ptithcm.core.param.LogInParam
import com.ptithcm.core.param.ProfileParam
import com.ptithcm.core.vo.Result

interface AuthRepository {
    suspend fun postLogIn(param: LogInParam): LiveData<Result<ObjectResponse<Account>>>
    suspend fun postSignUp(param: AccountParam): LiveData<Result<ObjectResponse<Account>>>
    suspend fun createProfile(param: ProfileParam): LiveData<Result<ObjectResponse<Profile>>>
    suspend fun getProfile(pid: Int): LiveData<Result<ObjectResponse<Profile>>>
    suspend fun logOut(): LiveData<Result<Void>>
    suspend fun requestForgotPassword(param: String): LiveData<Result<ObjectResponse<String>>>
    suspend fun changeFCMToken(accountId: Int?, token: String?): LiveData<Result<ObjectResponse<String>>>
}
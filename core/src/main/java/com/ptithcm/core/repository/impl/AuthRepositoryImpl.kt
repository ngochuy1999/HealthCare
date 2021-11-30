package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.api.ApiService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AccountParam
import com.ptithcm.core.param.LogInParam
import com.ptithcm.core.param.ProfileParam
import com.ptithcm.core.repository.AuthRepository
import com.ptithcm.core.vo.Result
import retrofit2.Response


class AuthRepositoryImpl(val api: ApiService, val apiHealthCare: ApiHealthCareService) : AuthRepository {
    override suspend fun requestForgotPassword(param: String): LiveData<Result<ObjectResponse<String>>> {
        return object : NetworkBoundResource<ObjectResponse<String>, ObjectResponse<String>>() {
            override fun processResponse(response: ObjectResponse<String>): ObjectResponse<String>? =
                response

            override suspend fun createCall(): Response<ObjectResponse<String>> =
                apiHealthCare.forgotPassword(param)
        }.build().asLiveData()
    }

    override suspend fun logOut(): LiveData<Result<Void>> {
        return object : NetworkBoundResource<Void, Void>() {

            override fun processResponse(response: Void): Void? {
                return null
            }

            override suspend fun createCall(): Response<Void> = apiHealthCare.logOut()

        }.build().asLiveData()
    }


    override suspend fun postSignUp(param: AccountParam): LiveData<Result<ObjectResponse<Account>>> {
        return object : NetworkBoundResource<ObjectResponse<Account>, ObjectResponse<Account>>() {
            override fun processResponse(response: ObjectResponse<Account>): ObjectResponse<Account>? = response

            override suspend fun createCall(): Response<ObjectResponse<Account>> = apiHealthCare.signUp(param)
        }.build().asLiveData()
    }

    override suspend fun createProfile(param: ProfileParam): LiveData<Result<ObjectResponse<Profile>>> {
        return object : NetworkBoundResource<ObjectResponse<Profile>, ObjectResponse<Profile>>() {
            override fun processResponse(response: ObjectResponse<Profile>): ObjectResponse<Profile>? = response

            override suspend fun createCall(): Response<ObjectResponse<Profile>> = apiHealthCare.createProfile(param)
        }.build().asLiveData()
    }

    override suspend fun getProfile(pid: Int): LiveData<Result<ObjectResponse<Profile>>> {
        return object : NetworkBoundResource<ObjectResponse<Profile>, ObjectResponse<Profile>>() {
            override fun processResponse(response: ObjectResponse<Profile>): ObjectResponse<Profile>? =
                response

            override suspend fun createCall(): Response<ObjectResponse<Profile>> =
                apiHealthCare.getProfile(pid)

        }.build().asLiveData()
    }

    override suspend fun postLogIn(param: LogInParam): LiveData<Result<ObjectResponse<Account>>> {
        return object : NetworkBoundResource<ObjectResponse<Account>, ObjectResponse<Account>>() {
            override fun processResponse(response: ObjectResponse<Account>): ObjectResponse<Account>? =
                response

            override suspend fun createCall(): Response<ObjectResponse<Account>> =
                apiHealthCare.signIn(param)

        }.build().asLiveData()
    }


}
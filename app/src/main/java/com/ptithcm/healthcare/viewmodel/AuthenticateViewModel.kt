package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AccountParam
import com.ptithcm.core.param.LogInParam
import com.ptithcm.core.param.ProfileParam
import com.ptithcm.core.repository.AuthRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class AuthenticateViewModel(private val repository: AuthRepository) : ViewModel() {
    val logInLiveData = MediatorLiveData<ObjectResponse<Account>>()
    val signUpLiveData = MediatorLiveData<ObjectResponse<Account>>()
    val profileLiveData = MediatorLiveData<ObjectResponse<Profile>>()
    val logOutLiveData = MediatorLiveData<String>()
    val forgotPasswordLiveData = MediatorLiveData<String>()
    val error = MutableLiveData<Pair<String, Int?>>()

    fun logIn(param: LogInParam) {
        viewModelScope.launch {
            logInLiveData.addSource(repository.postLogIn(param)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        logInLiveData.value = it.data
                    }
                }
            }
        }
    }

    fun signUp(account: AccountParam) {
        viewModelScope.launch {
            signUpLiveData.addSource(repository.postSignUp(account)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        signUpLiveData.value = it.data
                    }
                }
            }
        }
    }

    fun createProfile(prf: ProfileParam) {
        viewModelScope.launch {
            profileLiveData.addSource(repository.createProfile(prf)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        profileLiveData.value = it.data
                    }
                }
            }
        }
    }

    fun getProfile(pid : Int) {
        viewModelScope.launch {
            profileLiveData.addSource(repository.getProfile(pid)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        profileLiveData.value = it.data
                    }
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            logOutLiveData.addSource(repository.logOut()) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        logOutLiveData.value = it.message
                    }
                }
            }
        }
    }

    fun forgotPassword(param: String) {
        viewModelScope.launch {
            forgotPasswordLiveData.addSource(repository.requestForgotPassword(param)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        forgotPasswordLiveData.value = it.data?.message
                    }
                }
            }
        }
    }
}
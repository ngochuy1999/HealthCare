package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.*
import com.ptithcm.core.repository.HomeRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class HomeViewModel(private val repository : HomeRepository)  : ViewModel() {

    val doctorLiveData = MediatorLiveData<ArrayList<Doctor>>()
    val allDoctorLiveData = MediatorLiveData<ArrayList<Doctor>>()
    val doctorSpecializeLiveData = MediatorLiveData<ArrayList<Doctor>>()
    val specializeLiveData = MediatorLiveData<ArrayList<Specialize>>()
    val allSpecializeLiveData = MediatorLiveData<ArrayList<Specialize>>()
    val clinicLiveData = MediatorLiveData<ArrayList<Clinic>>()
    val notificationLiveData = MediatorLiveData<ArrayList<Notification>>()
    val shopInfo = MediatorLiveData<ShopInfo>()
    val networkState = MutableLiveData<Boolean>()
    val error = MutableLiveData<Pair<String, Int?>>()

    fun getTopSpecialize() {
        viewModelScope.launch {
            specializeLiveData.addSource(repository.getTopSpecialize()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        specializeLiveData.value = addSection(it.data?: arrayListOf())
                    }
                }
            }
        }
    }

    fun getAllSpecialize() {
        viewModelScope.launch {
            allSpecializeLiveData.addSource(repository.getAllSpecialize()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        allSpecializeLiveData.value = addSection(it.data?: arrayListOf())
                    }
                }
            }
        }
    }

    fun getShopInfo() {
        viewModelScope.launch {
            shopInfo.addSource(repository.getShopInfo()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        shopInfo.value = it.data?.data
                    }
                }
            }
        }
    }

    private fun addSection(mainCategories : ArrayList<Specialize>) : ArrayList<Specialize>{
        val results = arrayListOf<Specialize>()
        var count = 0
        mainCategories.forEachIndexed { index, item ->
            count++
            results.add(item)
            if ((index + 1) % 3 == 0){
                count = 0
            }
        }
        if (mainCategories.size % 3 != 0){
            count = 3 - mainCategories.size % 3
            while(count > 0){
                results.add(Specialize())
                count--
            }
        }
        return results
    }

    fun getTopDoctor() {
        viewModelScope.launch {
           doctorLiveData.addSource(repository.getTopDoctor()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        doctorLiveData.value = it.data?: arrayListOf()
                    }
                }
            }
        }
    }

    fun getAllDoctor() {
        viewModelScope.launch {
            allDoctorLiveData.addSource(repository.getAllDoctor()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        allDoctorLiveData.value = it.data?: arrayListOf()
                    }
                }
            }
        }
    }

    fun getDoctorBySpecialize(specializeId: Int) {
        viewModelScope.launch {
            doctorSpecializeLiveData.addSource(repository.getDoctorBySpecialize(specializeId)) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        doctorSpecializeLiveData.value = it.data?: arrayListOf()
                    }
                }
            }
        }
    }

    fun getNotification(accountId: Int?) {
        viewModelScope.launch {
           notificationLiveData.addSource(repository.getNotification(accountId)) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        notificationLiveData.value = it.data?: arrayListOf()
                    }
                }
            }
        }
    }

    fun getClinic() {
        viewModelScope.launch {
            clinicLiveData.addSource(repository.getClinic()) {
                when (it) {
                    is Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        clinicLiveData.value = it.data?: arrayListOf()
                    }
                }
            }
        }
    }


}
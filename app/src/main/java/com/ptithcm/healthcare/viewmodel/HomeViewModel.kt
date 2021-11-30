package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.Category
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.core.model.Specialize
import com.ptithcm.core.repository.HomeRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class HomeViewModel(private val repository : HomeRepository)  : ViewModel() {

    val doctorLiveData = MediatorLiveData<ArrayList<Doctor>>()
    val specializeLiveData = MediatorLiveData<ArrayList<Specialize>>()
    val shopInfo = MediatorLiveData<ShopInfo>()
    val networkState = MutableLiveData<Boolean>()
    val error = MutableLiveData<Pair<String, Int?>>()

    fun getListSpecialize() {
        viewModelScope.launch {
            specializeLiveData.addSource(repository.getListSpecialize()) {
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

    fun getListDoctor() {
        viewModelScope.launch {
           doctorLiveData.addSource(repository.getListDoctor()) {
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


}
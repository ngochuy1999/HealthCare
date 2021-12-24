package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.RequestCheckoutParam
import com.ptithcm.core.repository.CheckoutRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class CheckoutViewModel(private val repo: CheckoutRepository): ViewModel() {

    val resultRequestCheckout = MediatorLiveData<ObjectResponse<Any>>()
    val cancelResponse = MediatorLiveData<ObjectResponse<Any>>()

    val error = MutableLiveData<Pair<String, Int?>>()
    val isLoading = MutableLiveData<Boolean>()

    fun requestCheckout(checkoutParam: RequestCheckoutParam) {
        viewModelScope.launch {
            resultRequestCheckout.addSource(repo.requestCheckout(checkoutParam)) {
                when (it) {
                    Result.Loading -> {
                        isLoading.value = true
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        isLoading.value = false
                        if (it.data?.status == true)
                            resultRequestCheckout.value = it.data
                        else
                            error.value = Pair(it.data?.message ?: "", it.data?.code?.toInt())
                    }
                }
            }
        }
    }

    fun cancelBill(billId: Int?) {
        viewModelScope.launch {
            cancelResponse.addSource(repo.cancelBill(billId)) {
                when (it) {
                    Result.Loading -> {
                        isLoading.value = true
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        isLoading.value = false
                        if (it.data?.status == true)
                            cancelResponse.value = it.data
                        else
                            error.value = Pair(it.data?.message ?: "", it.data?.code?.toInt())
                    }
                }
            }
        }
    }
}
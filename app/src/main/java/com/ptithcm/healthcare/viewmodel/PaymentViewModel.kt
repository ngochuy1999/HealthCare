package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.Basket
import com.ptithcm.core.model.CreditCard
import com.ptithcm.core.model.ListLocation
import com.ptithcm.core.model.Location
import com.ptithcm.core.param.PaymentMethodParam
import com.ptithcm.core.param.UpdateAddressParam
import com.ptithcm.core.param.UpdatePaymentMethodParam
import com.ptithcm.core.repository.PaymentRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class PaymentViewModel(val repository: PaymentRepository) : ViewModel() {
    val getPaymentMethodLiveData = MediatorLiveData<ArrayList<CreditCard?>>()
    val deletePaymentMethodLiveData = MediatorLiveData<Void>()
    var error = MutableLiveData<Pair<String, Int?>>()


    fun getPaymentMethods() {
        viewModelScope.launch {
            getPaymentMethodLiveData.addSource(repository.getPaymentMethods()) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        getPaymentMethodLiveData.value = it.data
                    }
                }
            }
        }
    }


    fun deletePaymentMethod(cardID: String) {
        viewModelScope.launch {
            deletePaymentMethodLiveData.addSource(repository.deletePaymentMethod(cardID)) {
                when (it) {
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        deletePaymentMethodLiveData.value = it.data
                    }
                }
            }
        }
    }

}
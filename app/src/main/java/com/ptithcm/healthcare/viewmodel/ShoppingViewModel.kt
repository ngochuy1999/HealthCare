package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.core.model.ProductClothesDetail
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.core.repository.ShoppingCardRepository
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class ShoppingViewModel(val repo: ShoppingCardRepository) : ViewModel() {
    val cartResult = MediatorLiveData<ArrayList<ProductClothesDetail>>()
    val detailResult = MediatorLiveData<ProductClothesDetail>()
    val consultationResult = MediatorLiveData<ArrayList<MedicalBill>>()
    val consultationPatientResult = MediatorLiveData<ArrayList<MedicalBill>>()
    val addBillResult = MediatorLiveData<ObjectResponse<MedicalBill>>()
    val networkState = MutableLiveData<Boolean>()

    val error = MutableLiveData<Pair<String, Int?>>()
    val isLoading = MutableLiveData<Boolean>()

    fun getAllProductsInCart(ids: List<Int>) {
        viewModelScope.launch {
            cartResult.addSource(repo.getAllProductsInCart(ids)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        if (!it.data.isNullOrEmpty()) {
                            ObjectHandler.cart?.products = it.data ?: arrayListOf()
                            ObjectHandler.saveCartToPref()
                        }
                        cartResult.value = it.data
                    }
                }
            }
        }
    }


    fun getProdDetail(id: Int) {
        viewModelScope.launch {
            detailResult.addSource(repo.getProductDetail(id)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        detailResult.value = it.data?.data
                    }
                }
            }
        }
    }

    fun getAllConsult(id: Int) {
        viewModelScope.launch {
            consultationResult.addSource(repo.getAllConsult(id)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        consultationResult.value = it.data ?: arrayListOf()
                    }
                }
            }
        }
    }

    fun getAllConsultPatient(id: Int) {
        viewModelScope.launch {
            consultationPatientResult.addSource(repo.getAllConsultPatient(id)) {
                when (it) {
                    Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                        networkState.value = false
                    }
                    is Result.Success -> {
                        consultationPatientResult.value = it.data ?: arrayListOf()
                        networkState.value = false
                    }
                }
            }
        }
    }

    fun addMedicalBill(medicalBillParam: MedicalBillParam) {
        viewModelScope.launch {
            addBillResult.addSource(repo.addMedicalBill(medicalBillParam)) {
                when (it) {
                    Result.Loading -> {
                        networkState.value = true
                    }
                    is Result.Error -> {
                        networkState.value = false
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        networkState.value = false
                        addBillResult.value = it.data
                    }
                }
            }
        }
    }
}
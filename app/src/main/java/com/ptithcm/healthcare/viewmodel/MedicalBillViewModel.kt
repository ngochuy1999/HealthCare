package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.core.repository.MedicalBillRepository
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class MedicalBillViewModel(val repo: MedicalBillRepository) : ViewModel() {
    val consultationResult = MediatorLiveData<ArrayList<MedicalBill>>()
    val isLikeResult = MediatorLiveData<ObjectResponse<Boolean>>()
    val testFormResult = MediatorLiveData<ArrayList<TestForm>>()
    val testResultDetail = MediatorLiveData<ArrayList<TestResultDetail>>()
    val consultationPatientResult = MediatorLiveData<ArrayList<MedicalBill>>()
    val addBillResult = MediatorLiveData<ObjectResponse<MedicalBill>>()
    val networkState = MutableLiveData<Boolean>()

    val error = MutableLiveData<Pair<String, Int?>>()
    val isLoading = MutableLiveData<Boolean>()

    fun getTestForm(id: Int) {
        viewModelScope.launch {
            testFormResult.addSource(repo.getTestForm(id)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        testFormResult.value = it. data ?: arrayListOf()
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

    fun checkIsLike(pid: Int?, doctorId: Int) {
        viewModelScope.launch {
            isLikeResult.addSource(repo.checkIsLike(pid, doctorId)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        isLikeResult.value = it.data
                    }
                }
            }
        }
    }


    fun getImageResult(resultId: Int?) {
        viewModelScope.launch {
            testResultDetail.addSource(repo.getResultDetail(resultId)) {
                when (it) {
                    Result.Loading -> {
                    }
                    is Result.Error -> {
                        error.value = Pair(it.message, it.code)
                    }
                    is Result.Success -> {
                        testResultDetail.value = it. data ?: arrayListOf()
                    }
                }
            }
        }
    }

}
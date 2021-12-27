package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ptithcm.core.model.CreditCard

class ListenerViewModel: ViewModel() {
    val changePayment = MutableLiveData<CreditCard>()

}
package com.ptithcm.core.repository

import androidx.lifecycle.LiveData
import com.ptithcm.core.model.Basket
import com.ptithcm.core.model.CreditCard
import com.ptithcm.core.model.ListLocation
import com.ptithcm.core.model.Location
import com.ptithcm.core.param.PaymentMethodParam
import com.ptithcm.core.param.UpdateAddressParam
import com.ptithcm.core.param.UpdatePaymentMethodParam
import com.ptithcm.core.vo.Result

interface PaymentRepository {
    suspend fun getPaymentMethods(): LiveData<Result<ArrayList<CreditCard?>>>
    suspend fun deletePaymentMethod(cardId: String): LiveData<Result<Void>>
}
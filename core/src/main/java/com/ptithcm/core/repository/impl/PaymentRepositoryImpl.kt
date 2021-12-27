package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.api.ApiService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AddProductParam
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.core.repository.PaymentRepository
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.core.vo.MessageResponse
import com.ptithcm.core.vo.Result
import retrofit2.Response

class PaymentRepositoryImpl(val api: ApiService) : PaymentRepository {


    override suspend fun deletePaymentMethod(cardId: String): LiveData<Result<Void>> {
        return object : NetworkBoundResource<Void, Void>() {
            override fun processResponse(response: Void): Void?  = null

            override suspend fun createCall() = api.deletePaymentMethod(cardId)
        }.build().asLiveData()
    }

    override suspend fun getPaymentMethods(): LiveData<Result<ArrayList<CreditCard?>>> {
        return object : NetworkBoundResource<ArrayList<CreditCard?>, ArrayList<CreditCard?>>() {
            override fun processResponse(response: ArrayList<CreditCard?>) = response
            override suspend fun createCall(): Response<ArrayList<CreditCard?>> =
                api.getPaymentMethods()
        }.build().asLiveData()
    }


}
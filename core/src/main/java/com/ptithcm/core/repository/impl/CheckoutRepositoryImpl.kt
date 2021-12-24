package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.api.ApiService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.Checkout
import com.ptithcm.core.model.ShippingRate
import com.ptithcm.core.model.TaxCheckout
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.CheckoutParam
import com.ptithcm.core.param.DiscountParam
import com.ptithcm.core.param.RequestCheckoutParam
import com.ptithcm.core.param.ShippingRateParam
import com.ptithcm.core.repository.CheckoutRepository
import com.ptithcm.core.vo.Result
import retrofit2.Response

class CheckoutRepositoryImpl(val api: ApiService, val apiHealthCare: ApiHealthCareService) :
    CheckoutRepository {

    override suspend fun getShippingRates(
        id: Int?,
        identifier: String?
    ): LiveData<Result<ShippingRate>> {
        return object : NetworkBoundResource<ShippingRate, ShippingRate>() {
            override fun processResponse(response: ShippingRate) = response
            override suspend fun createCall(): Response<ShippingRate> =
                api.getShippingRates(id, identifier)
        }.build().asLiveData()
    }

    override suspend fun getCheckout(): LiveData<Result<Checkout>> {
        return object : NetworkBoundResource<Checkout, Checkout>() {
            override fun processResponse(response: Checkout) = response
            override suspend fun createCall(): Response<Checkout> = api.getCheckout()
        }.build().asLiveData()
    }

    override suspend fun addDiscountCode(param: DiscountParam): LiveData<Result<Checkout>> {
        return object : NetworkBoundResource<Checkout, Checkout>(){
            override fun processResponse(response: Checkout) = response
            override suspend fun createCall(): Response<Checkout> = api.addDiscountCode(param)
        }.build().asLiveData()
    }

    override suspend fun getTaxCheckout(id: Int?, identifier: String?): LiveData<Result<TaxCheckout>> {
        return object : NetworkBoundResource<TaxCheckout, TaxCheckout>(){
            override fun processResponse(response: TaxCheckout) = response
            override suspend fun createCall(): Response<TaxCheckout> = api.getTaxeCheckout(id, identifier)
        }.build().asLiveData()
    }

    override suspend fun shippingRatesPreOrder(id: Int?, identifier: String?, shippingRateParam: ShippingRateParam): LiveData<Result<Checkout>> {
        return object : NetworkBoundResource<Checkout, Checkout>(){
            override fun processResponse(response: Checkout) = response
            override suspend fun createCall(): Response<Checkout> =
                api.checkShippingRate(id, identifier, shippingRateParam)
        }.build().asLiveData()
    }

    override suspend fun checkoutPlaceOrder(checkoutParam: CheckoutParam): LiveData<Result<Checkout>> {
        return object : NetworkBoundResource<Checkout, Checkout>() {
            override fun processResponse(response: Checkout) = response
            override suspend fun createCall(): Response<Checkout> = api.checkout(checkoutParam)
        }.build().asLiveData()
    }

    override suspend fun requestCheckout(requestCheckoutParam: RequestCheckoutParam): LiveData<Result<ObjectResponse<Any>>> {
        return object : NetworkBoundResource<ObjectResponse<Any>, ObjectResponse<Any>>() {
            override fun processResponse(response: ObjectResponse<Any>) = response
            override suspend fun createCall(): Response<ObjectResponse<Any>> =
                apiHealthCare.requestCheckout(requestCheckoutParam)
        }.build().asLiveData()
    }

    override suspend fun cancelBill(billId: Int?): LiveData<Result<ObjectResponse<Any>>> {
        return object : NetworkBoundResource<ObjectResponse<Any>, ObjectResponse<Any>>() {
            override fun processResponse(response: ObjectResponse<Any>) = response
            override suspend fun createCall(): Response<ObjectResponse<Any>> =
                apiHealthCare.cancelBill(billId)
        }.build().asLiveData()
    }
}
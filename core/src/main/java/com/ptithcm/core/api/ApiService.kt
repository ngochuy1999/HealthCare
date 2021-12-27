package com.ptithcm.core.api

import com.ptithcm.core.model.*
import com.ptithcm.core.param.*
import com.ptithcm.core.vo.ApiResponse
import com.ptithcm.core.vo.MessageResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/users/login/")
    suspend fun logIn(@Body param: LogInParam): Response<Profile>


    @POST("/api/users/forgot_password/")
    suspend fun forgotPassword(@Body param: ForgotPasswordParam): Response<ApiResponse<String>>

    @GET("/api/users/me/")
    suspend fun getProfile(): Response<User>


    @GET("api/payments/stripe/payment-methods/")
    suspend fun getPaymentMethods(): Response<ArrayList<CreditCard?>>

    @DELETE("api/payments/stripe/payment-methods/{cardId}/")
    suspend fun deletePaymentMethod(@Path("cardId") cardId: String): Response<Void>


    @GET("/api/brands/store/{storeId}/")
    suspend fun getStoreBrand(@Path("storeId") storeId: Int?): Response<Brand>


    @PATCH("/api/payments/basket/")
    suspend fun updateBasket(@Body param: AddProductParam): Response<Basket>

    @GET("/api/payments/basket/")
    suspend fun getBasket(): Response<Basket>

    @DELETE("/api/payments/basket/{id}/")
    suspend fun removeItemFromBasket(@Path("id") id: Long): Response<MessageResponse>


    @GET("/api/payments/checkout/")
    suspend fun getCheckout(): Response<Checkout>

    @GET("/api/payments/checkout/shipping_rates/{brand_id}/{identifier}")
    suspend fun getShippingRates(
        @Path("brand_id") id: Int?,
        @Path("identifier") identifier: String?
    ): Response<ShippingRate>

    @PATCH("/api/payments/checkout/")
    suspend fun addDiscountCode(@Body param: DiscountParam): Response<Checkout>

    @GET("/api/payments/checkout/tax/{brand_id}/{identifier}")
    suspend fun getTaxeCheckout(
        @Path("brand_id") id: Int?,
        @Path("identifier") identifier: String?
    ): Response<TaxCheckout>

    @PATCH("/api/payments/checkout/shipping_rates/{brand_id}/{identifier}/")
    suspend fun checkShippingRate(
        @Path("brand_id") id: Int?,
        @Path("identifier") identifier: String?, @Body param: ShippingRateParam
    ): Response<Checkout>

    @POST("/api/payments/checkout/payment/")
    suspend fun checkout(@Body param: CheckoutParam): Response<Checkout>

}
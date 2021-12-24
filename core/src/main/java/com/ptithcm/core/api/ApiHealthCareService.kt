package com.ptithcm.core.api

import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.*
import com.ptithcm.core.vo.ListResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiHealthCareService {
    /*-----------------------------*/
    /* Auth */
    @POST("/api/login")
    suspend fun signIn(@Body loginParam: LogInParam): Response<ObjectResponse<Account>>

    @DELETE("/api/logout")
    suspend fun logOut(): Response<Void>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): Response<ObjectResponse<String>>

    @PUT("/api/fcm-token")
    suspend fun changeFCMToken(@Query("id") accountId: Int?,
                               @Query("token") token: String?): Response<ObjectResponse<String>>

    @POST("/api/signUp")
    suspend fun signUp(@Body acc: AccountParam): Response<ObjectResponse<Account>>

    @POST("/api/create-profile")
    suspend fun createProfile(@Body profileParam: ProfileParam): Response<ObjectResponse<Profile>>

    @GET("/api/profile")
    suspend fun getProfile(@Query("pid") pid: Int): Response<ObjectResponse<Profile>>

    @PUT("/api/password")
    suspend fun changePassword(@Body param: ChangePassParam): Response<ObjectResponse<Account>>

    @PUT("/api/changeInfo")
    suspend fun updateProfile(@Body param: EditAccountParam): Response<ObjectResponse<Profile>>

    @PUT("/api/avatar")
    suspend fun updateImage(@Query("url") url: String,
                            @Query("id") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ObjectResponse<Account>>

    @PUT("/api/cover")
    suspend fun updateCover(@Query("url") url: String,
                            @Query("id") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ObjectResponse<Account>>
    /*-----------------------------*/
    /* Specialize */
    @GET("/api/topSpeciality")
    suspend fun getTopSpecialize(): Response<ArrayList<Specialize>>

    @GET("/api/allSpeciality")
    suspend fun getAllSpecialize(): Response<ArrayList<Specialize>>

    /*-----------------------------*/
    /* List Doctor */
    @GET("/api/topDoctor")
    suspend fun getTopDoctor(): Response<ArrayList<Doctor>>

    @GET("/api/allDoctor")
    suspend fun getAllDoctor(): Response<ArrayList<Doctor>>

    @GET("/api/doctor_specialize")
    suspend fun getDoctorBySpecialize(@Query("specialId") specialId: Int): Response<ArrayList<Doctor>>


    /*-----------------------------*/
    /* Product*/
    @POST("/api/allProductsOfCategory")
    suspend fun getProducts(@Body param: ProductsOfCategoryRequestParam): Response<ListResponse<ProductClothes>>

    @POST("/api/allProductsOfProvider")
    suspend fun getProductsProvider(@Body param: ProductsOfProviderRequestParam): Response<ListResponse<ProductClothes>>

    @GET("/api/productDetail")
    suspend fun getProductDetail(
        @Query("productID") providerId: Int,
        @Query("accountId") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ObjectResponse<ProductClothesDetail>>

    @POST("/api/get-all-product-cart")
    suspend fun getAllProductsInCart(
        @Body ids: List<Int>
    ): Response<ArrayList<ProductClothesDetail>>

    @POST("/api/products-search")
    suspend fun getRefineProducts(
        @Body searchParams: SearchParams?
    ): Response<ListResponse<ProductClothes>>

    /*-----------------------------*/
    /* Favorite Doctor*/
    @GET("/api/favoriteDoctor")
    suspend fun getAllWishList(
        @Query("accountId") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ArrayList<Doctor>>

    @PUT("/api/checkFavoriteProduct")
    suspend fun addAndRemoveToWishList(
        @Query("productId") providerId: Int?,
        @Query("accountId") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ObjectResponse<Int>>


    /*-----------------------------*/
    /* Providers*/
    @GET("/api/providers")
    suspend fun getProviders(): Response<ArrayList<Provider>>

    @GET("/api/providerDetail")
    suspend fun getDetailProvider(
        @Query("providerId") providerId: Int?
    ): Response<Provider>

    /*-----------------------------*/
    /* Address*/
    @GET("/api/address")
    suspend fun getAllAddress(@Query("accountId") accountId: Int? = CoreApplication.instance.account?.accountId): Response<ArrayList<ShoppingAddress>>

    @POST("/api/add-addess")
    suspend fun addAddress(@Body param: ShoppingAddress): Response<ObjectResponse<Int>>

    @PUT("/api/edit-addess")
    suspend fun updateAddress(@Body param: ShoppingAddress): Response<ObjectResponse<Int>>

    @DELETE("/api/del-addess")
    suspend fun deleteAddress(@Query("addressId") addressId: Int?): Response<ObjectResponse<Int>>

    /*-----------------------------*/
    /* Invoice*/
    @GET("/api/allInvoice")
    suspend fun getAllInvoices(
        @Query("pageSize") pageSize: Int = 20,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("statusId") statusId: Int,
        @Query("accountId") accountId: Int? = CoreApplication.instance.account?.accountId
    ): Response<ListResponse<Invoice>>

    @GET("/api/invoice-detail")
    suspend fun getInvoiceDetail(@Query("invoicecId") invoiceId: Int?): Response<ObjectResponse<InvoiceDetail>>

    @POST("/api/charge")
    suspend fun requestCheckout(@Body param: RequestCheckoutParam): Response<ObjectResponse<Any>>


    /*-----------------------------*/
    /* MedicalBill*/
    @POST("/api/medical-bill")
    suspend fun addMedicalBill(@Body medicalBillParam: MedicalBillParam?): Response<ObjectResponse<MedicalBill>>

    /*-----------------------------*/
    /* Consult*/
    @GET("/api/consultation")
    suspend fun getAllConsult(@Query("doctorId") doctorId: Int?): Response<ArrayList<MedicalBill>>

    /* Consult Patient*/
    @GET("/api/consultation-patient")
    suspend fun getAllConsultPatient(@Query("PID") PID: Int?): Response<ArrayList<MedicalBill>>


    /*-----------------------------*/
    /* Test Form */
    @GET("/api/test-form")
    suspend fun getTestForm(@Query("billId") billId: Int?): Response<ArrayList<TestForm>>


    /*-----------------------------*/
    /* Question*/
    @GET("/api/getQuestions")
    suspend fun getAllQuestion(
        @Query("pageSize") pageSize: Int = 20,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("productId") productId: Int
    ): Response<ListResponse<Question>>

    @POST("/api/addQuestion")
    suspend fun addQuestion(@Body param: Question): Response<ObjectResponse<Int>>

    @PUT("/api/updateQuestion")
    suspend fun updateQuestion(@Body param: Question): Response<ObjectResponse<Int>>

    @DELETE("/api/delQuestion")
    suspend fun delQuestion(
        @Query("questionID") addressId: Int?,
        @Query("isSubQuestion") isSubQuestion: Int?
    ): Response<ObjectResponse<Int>>

    @GET("/api/getQuestionsCount")
    suspend fun getQuestionCount(
        @Query("productId") productId: Int?
    ): Response<ObjectResponse<Int>>


    /*-----------------------------*/
    /* Rating */
    @GET("/api/getRating")
    suspend fun getRatings(
        @Query("pageSize") pageSize: Int = 20,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("productId") productId: Int
    ): Response<ListResponse<Rating>>

    @GET("/api/getRatingById")
    suspend fun getRating(
        @Query("ratingID") ratingID: Int
    ): Response<ObjectResponse<Rating>>

    @POST("/api/addRating")
    suspend fun addRating(@Body param: Rating): Response<ObjectResponse<Int>>

    @PUT("/api/updateRating")
    suspend fun updateRating(@Body ratingObj: Rating): Response<ObjectResponse<Int>>

    @DELETE("/api/delRating")
    suspend fun delRating(
        @Query("ratingID") ratingID: Int?
    ): Response<ObjectResponse<Int>>

    @GET("/api/getRatingProduct")
    suspend fun getRatingProduct(
        @Query("productId") productId: Int?
    ): Response<ObjectResponse<RatingProduct>>

    @GET("/api/getRatingDetail")
    suspend fun getRatingDetail(
        @Query("ratingId") ratingId: Int?
    ): Response<ObjectResponse<Rating>>

    @GET("/api/checkRatingProduct")
    suspend fun checkRatingProduct(
        @Query("productId") productId: Int?,
        @Query("accId") accId: Int?,
        @Query("colorId") colorId: Int?,
        @Query("sizeId") sizeId: Int?,
        @Query("orderId") orderId: Int?
    ): Response<ObjectResponse<Int>>

    @GET("/api/getShopInfo")
    suspend fun getShopInfo(): Response<ObjectResponse<ShopInfo>>

    @GET("/api/isLike")
    suspend fun chekIsLike(@Query("pid") pid: Int?,
                           @Query("doctorId") doctorId: Int): Response<ObjectResponse<Boolean>>

    @GET("/api/medical-record")
    suspend fun getMedicalRecord(@Query("pid") pid: Int?): Response<ArrayList<MedicalRecord>>

    @GET("/api/test-result")
    suspend fun getTestResult(@Query("pid") accountId: Int?): Response<ArrayList<TestResult>>

    @GET("/api/treatment-regiment")
    suspend fun getTreatmentRegiment(@Query("medicalRecordId") recordId: Int?): Response<TreatmentRegiment>

    @GET("/api/notification")
    suspend fun getNotification(@Query("pid")accountId: Int?): Response<ArrayList<Notification>>

    @GET("/api/clinic")
    suspend fun getClinic(): Response<ArrayList<Clinic>>

    @GET("/api/info")
    suspend fun getVersionApp(): Response<Info>

    @PUT("/api/cancel")
    suspend fun cancelBill(@Query("medicalId")billId: Int?): Response<ObjectResponse<Any>>

    @GET("/api/test-result_detail")
    suspend fun getResultDetail(@Query("resultId") resultId: Int?): Response<ArrayList<TestResultDetail>>


}


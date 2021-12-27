package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.api.ApiService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AddProductParam
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.core.repository.MedicalBillRepository
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.core.vo.MessageResponse
import com.ptithcm.core.vo.Result
import retrofit2.Response

class MedicalBillRepositoryImpl(val api: ApiService, val apiHealthCareService: ApiHealthCareService) :
    MedicalBillRepository {

    override suspend fun updateBasket(param: AddProductParam): LiveData<Result<Basket>> {
        return object : NetworkBoundResource<Basket, Basket>() {
            override suspend fun createCall(): Response<Basket> = api.updateBasket(param)

            override fun processResponse(response: Basket): Basket? {
                response.product_variants.forEach {
                    it.product_variant.apply {
                        product?.isAddProduct = ObjectHandler.isInWishList(product?.id)
                        checkIfWrongPrice()
                    }
                }
                return response
            }
        }.build().asLiveData()
    }

    override suspend fun getAllCard(): LiveData<Result<Basket>> {
        return object : NetworkBoundResource<Basket, Basket>() {
            override suspend fun createCall(): Response<Basket> = api.getBasket()

            override fun processResponse(response: Basket): Basket? {
                response.product_variants.forEach {
                    it.product_variant.apply {
                        product?.isAddProduct = ObjectHandler.isInWishList(product?.id)
                        checkIfWrongPrice()
                    }
                }
                return response
            }
        }.build().asLiveData()
    }

    override suspend fun removeFromBasket(id: Long): LiveData<Result<MessageResponse>> {
        return object : NetworkBoundResource<MessageResponse, MessageResponse>() {
            override fun processResponse(response: MessageResponse) = response
            override suspend fun createCall(): Response<MessageResponse> =
                api.removeItemFromBasket(id)
        }.build().asLiveData()
    }

    override suspend fun getProductDetail(id: Int?): LiveData<Result<ObjectResponse<ProductClothesDetail>>> {
        return object :
            NetworkBoundResource<ObjectResponse<ProductClothesDetail>, ObjectResponse<ProductClothesDetail>>() {
            override suspend fun createCall(): Response<ObjectResponse<ProductClothesDetail>> =
                apiHealthCareService.getProductDetail(id ?: 0)

            override fun processResponse(response: ObjectResponse<ProductClothesDetail>): ObjectResponse<ProductClothesDetail>? {
                return response
            }
        }.build().asLiveData()
    }

    override suspend fun getAllProductsInCart(ids: List<Int>): LiveData<Result<ArrayList<ProductClothesDetail>>> {
        return object :
            NetworkBoundResource<ArrayList<ProductClothesDetail>, ArrayList<ProductClothesDetail>>() {
            override suspend fun createCall(): Response<ArrayList<ProductClothesDetail>> =
                apiHealthCareService.getAllProductsInCart(ids)

            override fun processResponse(response: ArrayList<ProductClothesDetail>): ArrayList<ProductClothesDetail>? {
                response.forEachIndexed { i, newProduct ->
                    val curProduct = ObjectHandler.cart?.products?.getOrNull(i)
                    if (newProduct.id != curProduct?.id)
                        return@forEachIndexed

                    newProduct.run {
                        selectedColor = curProduct.selectedColor
                        selectedSize = curProduct.selectedSize
                        quantityInCart = curProduct.quantityInCart

                        sizesColors?.forEach {  it.price = price}

                        hasChangedPrice =
                            newProduct.price != curProduct.price || newProduct.typePromotion != curProduct.typePromotion || newProduct.valuePromotion != curProduct.valuePromotion

                        val newSizeAndColor = newProduct.getSizeAndColorById(
                            sizeId = curProduct.selectedSize?.id,
                            colorId = curProduct.selectedColor?.id
                        )
                        val quantityInStock = newSizeAndColor?.quantity ?: 0
                        hasChangedQuantity =
                            curProduct.quantityInCart?.quantity ?: 0 > quantityInStock

                        hasChanged = hasChangedPrice || hasChangedQuantity

                    }
                }
                return response
            }
        }.build().asLiveData()
    }

    override suspend fun getShopDetail(id: Int?): LiveData<Result<Brand>> {
        return object : NetworkBoundResource<Brand, Brand>() {
            override fun processResponse(response: Brand) = response
            override fun shouldFetch(data: Brand?): Boolean = true
            override suspend fun createCall(): Response<Brand> = api.getStoreBrand(id)
        }.build().asLiveData()
    }

    override suspend fun getAllConsult(id: Int): LiveData<Result<ArrayList<MedicalBill>>> {
        return object : NetworkBoundResource<ArrayList<MedicalBill>, ArrayList<MedicalBill>>() {
            override fun processResponse(response: ArrayList<MedicalBill>) = response
            override suspend fun createCall(): Response<ArrayList<MedicalBill>> =
                apiHealthCareService.getAllConsult(id)
        }.build().asLiveData()
    }

    override suspend fun getTestForm(id: Int): LiveData<Result<ArrayList<TestForm>>> {
        return object : NetworkBoundResource<ArrayList<TestForm>, ArrayList<TestForm>>() {
            override fun processResponse(response: ArrayList<TestForm>) = response
            override suspend fun createCall(): Response<ArrayList<TestForm>> =
                apiHealthCareService.getTestForm(id)
        }.build().asLiveData()
    }


    override suspend fun getAllConsultPatient(id: Int): LiveData<Result<ArrayList<MedicalBill>>> {
        return object : NetworkBoundResource<ArrayList<MedicalBill>, ArrayList<MedicalBill>>() {
            override fun processResponse(response: ArrayList<MedicalBill>) = response
            override suspend fun createCall(): Response<ArrayList<MedicalBill>> =
                apiHealthCareService.getAllConsultPatient(id)
        }.build().asLiveData()
    }

    override suspend fun addMedicalBill(medicalBillParam: MedicalBillParam): LiveData<Result<ObjectResponse<MedicalBill>>> {
        return object : NetworkBoundResource<ObjectResponse<MedicalBill>, ObjectResponse<MedicalBill>>() {
            override fun processResponse(response: ObjectResponse<MedicalBill>) = response
            override suspend fun createCall(): Response<ObjectResponse<MedicalBill>> =
                apiHealthCareService.addMedicalBill(medicalBillParam)
        }.build().asLiveData()
    }

    override suspend fun checkIsLike(pid: Int?, doctorId: Int): LiveData<Result<ObjectResponse<Boolean>>> {
        return object : NetworkBoundResource<ObjectResponse<Boolean>, ObjectResponse<Boolean>>() {
            override fun processResponse(response: ObjectResponse<Boolean>) = response
            override suspend fun createCall(): Response<ObjectResponse<Boolean>> =
                apiHealthCareService.chekIsLike(pid,doctorId)
        }.build().asLiveData()
    }


    override suspend fun getResultDetail(resultId: Int?): LiveData<Result<ArrayList<TestResultDetail>>> {
        return object : NetworkBoundResource<ArrayList<TestResultDetail>, ArrayList<TestResultDetail>>() {
            override fun processResponse(response: ArrayList<TestResultDetail>) = response
            override suspend fun createCall(): Response<ArrayList<TestResultDetail>> =
                apiHealthCareService.getResultDetail(resultId)
        }.build().asLiveData()
    }
}
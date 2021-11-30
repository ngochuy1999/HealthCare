package com.ptithcm.core.repository

import androidx.lifecycle.LiveData
import com.ptithcm.core.model.Basket
import com.ptithcm.core.model.Brand
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.core.model.ProductClothesDetail
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.AddProductParam
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.core.vo.MessageResponse
import com.ptithcm.core.vo.Result

interface ShoppingCardRepository {
    suspend fun updateBasket(param: AddProductParam): LiveData<Result<Basket>>

    suspend fun getAllCard(): LiveData<Result<Basket>>

    suspend fun removeFromBasket(id: Long): LiveData<Result<MessageResponse>>

    suspend fun getShopDetail(id: Int?): LiveData<Result<Brand>>

    suspend fun getProductDetail(id: Int?): LiveData<Result<ObjectResponse<ProductClothesDetail>>>

    suspend fun getAllProductsInCart(ids: List<Int>): LiveData<Result<ArrayList<ProductClothesDetail>>>

    suspend fun getAllConsult(id: Int): LiveData<Result<ArrayList<MedicalBill>>>

    suspend fun getAllConsultPatient(id: Int): LiveData<Result<ArrayList<MedicalBill>>>

    suspend fun addMedicalBill(medicalBillParam: MedicalBillParam): LiveData<Result<ObjectResponse<MedicalBill>>>
}
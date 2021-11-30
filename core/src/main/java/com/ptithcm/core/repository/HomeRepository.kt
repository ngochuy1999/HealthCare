package com.ptithcm.core.repository

import androidx.lifecycle.LiveData
import com.ptithcm.core.model.Category
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.core.model.Specialize
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.vo.Result

interface HomeRepository {
    suspend fun getListSpecialize(): LiveData<Result<ArrayList<Specialize>>>
    suspend fun getShopInfo(): LiveData<Result<ObjectResponse<ShopInfo>>>
    suspend fun getListDoctor(): LiveData<Result<ArrayList<Doctor>>>
}
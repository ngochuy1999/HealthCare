package com.ptithcm.core.repository

import androidx.lifecycle.LiveData
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.vo.Result

interface HomeRepository {
    suspend fun getTopSpecialize(): LiveData<Result<ArrayList<Specialize>>>
    suspend fun getAllSpecialize(): LiveData<Result<ArrayList<Specialize>>>
    suspend fun getShopInfo(): LiveData<Result<ObjectResponse<ShopInfo>>>
    suspend fun getTopDoctor(): LiveData<Result<ArrayList<Doctor>>>
    suspend fun getAllDoctor(): LiveData<Result<ArrayList<Doctor>>>
    suspend fun getDoctorBySpecialize(specializeId: Int): LiveData<Result<ArrayList<Doctor>>>
    suspend fun getNotification(accountId: Int?): LiveData<Result<ArrayList<Notification>>>
    suspend fun getClinic(): LiveData<Result<ArrayList<Clinic>>>
}
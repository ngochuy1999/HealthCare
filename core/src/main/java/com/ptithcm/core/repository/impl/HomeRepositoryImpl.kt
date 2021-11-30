package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.Category
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.core.model.Specialize
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.repository.HomeRepository
import com.ptithcm.core.vo.Result
import retrofit2.Response

class HomeRepositoryImpl(val apiHealthCare: ApiHealthCareService) : HomeRepository {
    override suspend fun getListSpecialize(): LiveData<Result<ArrayList<Specialize>>> {
        return object : NetworkBoundResource<ArrayList<Specialize>, ArrayList<Specialize>>() {
            override fun processResponse(response: ArrayList<Specialize>) = response
            override suspend fun createCall(): Response<ArrayList<Specialize>> =
                apiHealthCare.getListSpecialize()
        }.build().asLiveData()
    }

    override suspend fun getListDoctor(): LiveData<Result<ArrayList<Doctor>>> {
        return object : NetworkBoundResource<ArrayList<Doctor>, ArrayList<Doctor>>() {
            override fun processResponse(response: ArrayList<Doctor>) = response
            override suspend fun createCall(): Response<ArrayList<Doctor>> =
                apiHealthCare.getListDoctor()
        }.build().asLiveData()
    }

    override suspend fun getShopInfo(): LiveData<Result<ObjectResponse<ShopInfo>>> {
        return object : NetworkBoundResource<ObjectResponse<ShopInfo>,ObjectResponse<ShopInfo>>() {
            override fun processResponse(response: ObjectResponse<ShopInfo>) = response
            override suspend fun createCall(): Response<ObjectResponse<ShopInfo>> =
                apiHealthCare.getShopInfo()
        }.build().asLiveData()
    }
}
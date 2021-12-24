package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.repository.HomeRepository
import com.ptithcm.core.vo.Result
import retrofit2.Response

class HomeRepositoryImpl(val apiHealthCare: ApiHealthCareService) : HomeRepository {
    override suspend fun getTopSpecialize(): LiveData<Result<ArrayList<Specialize>>> {
        return object : NetworkBoundResource<ArrayList<Specialize>, ArrayList<Specialize>>() {
            override fun processResponse(response: ArrayList<Specialize>) = response
            override suspend fun createCall(): Response<ArrayList<Specialize>> =
                apiHealthCare.getTopSpecialize()
        }.build().asLiveData()
    }

    override suspend fun getAllSpecialize(): LiveData<Result<ArrayList<Specialize>>> {
        return object : NetworkBoundResource<ArrayList<Specialize>, ArrayList<Specialize>>() {
            override fun processResponse(response: ArrayList<Specialize>) = response
            override suspend fun createCall(): Response<ArrayList<Specialize>> =
                apiHealthCare.getAllSpecialize()
        }.build().asLiveData()
    }

    override suspend fun getTopDoctor(): LiveData<Result<ArrayList<Doctor>>> {
        return object : NetworkBoundResource<ArrayList<Doctor>, ArrayList<Doctor>>() {
            override fun processResponse(response: ArrayList<Doctor>) = response
            override suspend fun createCall(): Response<ArrayList<Doctor>> =
                apiHealthCare.getTopDoctor()
        }.build().asLiveData()
    }

    override suspend fun getAllDoctor(): LiveData<Result<ArrayList<Doctor>>> {
        return object : NetworkBoundResource<ArrayList<Doctor>, ArrayList<Doctor>>() {
            override fun processResponse(response: ArrayList<Doctor>) = response
            override suspend fun createCall(): Response<ArrayList<Doctor>> =
                apiHealthCare.getAllDoctor()
        }.build().asLiveData()
    }


    override suspend fun getShopInfo(): LiveData<Result<ObjectResponse<ShopInfo>>> {
        return object : NetworkBoundResource<ObjectResponse<ShopInfo>,ObjectResponse<ShopInfo>>() {
            override fun processResponse(response: ObjectResponse<ShopInfo>) = response
            override suspend fun createCall(): Response<ObjectResponse<ShopInfo>> =
                apiHealthCare.getShopInfo()
        }.build().asLiveData()
    }

    override suspend fun getDoctorBySpecialize(specializeId: Int): LiveData<Result<ArrayList<Doctor>>> {
        return object : NetworkBoundResource<ArrayList<Doctor>, ArrayList<Doctor>>() {
            override fun processResponse(response: ArrayList<Doctor>) = response
            override suspend fun createCall(): Response<ArrayList<Doctor>> =
                apiHealthCare.getDoctorBySpecialize(specializeId)
        }.build().asLiveData()
    }

    override suspend fun getNotification(accountId: Int?): LiveData<Result<ArrayList<Notification>>> {
        return object : NetworkBoundResource<ArrayList<Notification>, ArrayList<Notification>>() {
            override fun processResponse(response: ArrayList<Notification>) = response
            override suspend fun createCall(): Response<ArrayList<Notification>> =
                apiHealthCare.getNotification(accountId)
        }.build().asLiveData()
    }

    override suspend fun getClinic(): LiveData<Result<ArrayList<Clinic>>> {
        return object : NetworkBoundResource<ArrayList<Clinic>, ArrayList<Clinic>>() {
            override fun processResponse(response: ArrayList<Clinic>) = response
            override suspend fun createCall(): Response<ArrayList<Clinic>> =
                apiHealthCare.getClinic()
        }.build().asLiveData()
    }
}
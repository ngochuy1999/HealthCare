package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.ProductClothes
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.repository.WishListRepository
import com.ptithcm.core.vo.Result
import retrofit2.Response

class WishListRepositoryImpl(val apiHealthCare: ApiHealthCareService) : WishListRepository {

    override suspend fun getWishList(): LiveData<Result<ArrayList<Doctor>>> {
        return object :
            NetworkBoundResource<ArrayList<Doctor>, ArrayList<Doctor>>() {
            override suspend fun createCall(): Response<ArrayList<Doctor>> =
                apiHealthCare.getAllWishList()

            override fun processResponse(response: ArrayList<Doctor>): ArrayList<Doctor>? {
                return response
            }
        }.build().asLiveData()
    }

    override suspend fun addAndRemoveToWishList(id: Int?): LiveData<Result<ObjectResponse<Int>>> {
        return object : NetworkBoundResource<ObjectResponse<Int>, ObjectResponse<Int>>() {
            override fun processResponse(response: ObjectResponse<Int>) = response
            override suspend fun createCall(): Response<ObjectResponse<Int>> =
                apiHealthCare.addAndRemoveToWishList(id)
        }.build().asLiveData()
    }
}
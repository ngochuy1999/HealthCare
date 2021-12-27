package com.ptithcm.core.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.toLiveData
import com.ptithcm.core.api.ApiHealthCareService
import com.ptithcm.core.api.ApiService
import com.ptithcm.core.data.remote.BaseDataSourceFactory
import com.ptithcm.core.data.remote.NetworkBoundResource
import com.ptithcm.core.model.*
import com.ptithcm.core.model.wish.ObjectResponse
import com.ptithcm.core.param.ChangePassParam
import com.ptithcm.core.param.EditAccountParam
import com.ptithcm.core.param.UpdateAddressParam
import com.ptithcm.core.repository.UserRepository
import com.ptithcm.core.util.PAGE_SIZE
import com.ptithcm.core.vo.ItemViewModel
import com.ptithcm.core.vo.ListResponse
import com.ptithcm.core.vo.Listing
import com.ptithcm.core.vo.Result
import okhttp3.ResponseBody
import retrofit2.Response

class UserRepositoryImpl (val api: ApiService, val apiHealthCare: ApiHealthCareService): UserRepository {


    override suspend fun changePassword(param: ChangePassParam): LiveData<Result<ObjectResponse<Account>>> {
        return object : NetworkBoundResource<ObjectResponse<Account>, ObjectResponse<Account>>(){
            override fun processResponse(response: ObjectResponse<Account>) = response
            override suspend fun createCall(): Response<ObjectResponse<Account>> = apiHealthCare.changePassword(param)

        }.build().asLiveData()
    }

    override suspend fun updateProfile(param: EditAccountParam): LiveData<Result<ObjectResponse<Profile>>> {
        return object : NetworkBoundResource<ObjectResponse<Profile>, ObjectResponse<Profile>>(){
            override fun processResponse(response: ObjectResponse<Profile>) = response
            override suspend fun createCall(): Response<ObjectResponse<Profile>> =
                apiHealthCare.updateProfile(param)

        }.build().asLiveData()
    }

    override suspend fun updateImage(url: String): LiveData<Result<ObjectResponse<Account>>> {
        return object : NetworkBoundResource<ObjectResponse<Account>, ObjectResponse<Account>>(){
            override fun processResponse(response: ObjectResponse<Account>) = response
            override suspend fun createCall(): Response<ObjectResponse<Account>> =
                apiHealthCare.updateImage(url)

        }.build().asLiveData()
    }

    override suspend fun updateCover(url: String): LiveData<Result<ObjectResponse<Account>>> {
        return object : NetworkBoundResource<ObjectResponse<Account>, ObjectResponse<Account>>(){
            override fun processResponse(response: ObjectResponse<Account>) = response
            override suspend fun createCall(): Response<ObjectResponse<Account>> =
                apiHealthCare.updateCover(url)

        }.build().asLiveData()
    }

    override suspend fun getProfile(): LiveData<Result<User>> {
        return object : NetworkBoundResource<User, User>() {
            override fun processResponse(response: User) = response
            override suspend fun createCall(): Response<User> = api.getProfile()
        }.build().asLiveData()
    }

    override suspend fun getMedicalRecord(pid: Int?): LiveData<Result<ArrayList<MedicalRecord>>> {
        return object :
            NetworkBoundResource<ArrayList<MedicalRecord>, ArrayList<MedicalRecord>>() {
            override fun processResponse(response: ArrayList<MedicalRecord>) = response
            override suspend fun createCall(): Response<ArrayList<MedicalRecord>> =
                apiHealthCare.getMedicalRecord(pid)
        }.build().asLiveData()
    }

    override suspend fun getTestResult(accountId: Int?): LiveData<Result<ArrayList<TestResult>>> {
        return object :
            NetworkBoundResource<ArrayList<TestResult>, ArrayList<TestResult>>() {
            override fun processResponse(response: ArrayList<TestResult>) = response
            override suspend fun createCall(): Response<ArrayList<TestResult>> =
                apiHealthCare.getTestResult(accountId)
        }.build().asLiveData()
    }

    override suspend fun getTreatmentRegiment(recordId: Int?): LiveData<Result<TreatmentRegiment>> {
        return object :
            NetworkBoundResource<TreatmentRegiment, TreatmentRegiment>() {
            override fun processResponse(response: TreatmentRegiment) = response
            override suspend fun createCall(): Response<TreatmentRegiment> =
                apiHealthCare.getTreatmentRegiment(recordId)
        }.build().asLiveData()
    }

    override suspend fun getAppInfo(): LiveData<Result<Info>> {
        return object :
            NetworkBoundResource<Info, Info>() {
            override fun processResponse(response: Info) = response
            override suspend fun createCall(): Response<Info> =
                apiHealthCare.getVersionApp()
        }.build().asLiveData()
    }

    override suspend fun getPagingAllInvoices(
        pageSize: Int,
        pageNumber: Int,
        statusId: Int
    ): Listing<ItemViewModel> {
        val sourceFactory =
                object :
                        BaseDataSourceFactory<Invoice, ItemViewModel>(status = MutableLiveData()) {
                    override suspend fun createXCall(page: Int): Response<ListResponse<Invoice>> {
                        return apiHealthCare.getAllInvoices(pageSize, page, statusId)
                    }

                    override suspend fun handleXResponse(
                            items: ListResponse<Invoice>, firstLoad: Boolean
                    ): List<ItemViewModel> {
                        return items.results
                    }
                }

        val pagedLiveData = sourceFactory.toLiveData(pageSize = PAGE_SIZE)

        return Listing(
            result = pagedLiveData,
            status = sourceFactory.status,
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            }
        )
    }

    override suspend fun getInvoiceDetail(invoiceId: Int?): LiveData<Result<ObjectResponse<InvoiceDetail>>> {
        return object :
            NetworkBoundResource<ObjectResponse<InvoiceDetail>, ObjectResponse<InvoiceDetail>>() {
            override fun processResponse(response: ObjectResponse<InvoiceDetail>) = response
            override suspend fun createCall(): Response<ObjectResponse<InvoiceDetail>> =
                apiHealthCare.getInvoiceDetail(invoiceId)
        }.build().asLiveData()
    }
}
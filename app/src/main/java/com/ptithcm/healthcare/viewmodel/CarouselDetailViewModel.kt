package com.ptithcm.healthcare.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.ptithcm.core.model.SearchParams
import com.ptithcm.core.param.ProductsOfCategoryRequestParam
import com.ptithcm.core.param.ProductsOfProviderRequestParam
import com.ptithcm.core.repository.CarouselDetailRepository
import com.ptithcm.core.vo.ItemViewModel
import com.ptithcm.core.vo.Result
import kotlinx.coroutines.launch

class CarouselDetailViewModel(private val repository: CarouselDetailRepository) : ViewModel() {

    val networkState = MutableLiveData<Boolean>()
    val productLoadStatusX = MutableLiveData<Result<ItemViewModel>>()


    val refineProductLiveData = MediatorLiveData<PagedList<ItemViewModel>>()
    val networkStateRefine = MutableLiveData<Boolean>()
    fun searchPagingProducts(searchParams: SearchParams?) {
        searchParams ?: return
        viewModelScope.launch {
            val request = repository.getPagingRefineProduct(searchParams)
            refineProductLiveData.addSource(request.result) {
                refineProductLiveData.value = it
            }
            refineProductLiveData.addSource(request.status) {
                productLoadStatusX.value = it
                when (it) {
                    is Result.Loading -> {
                        networkStateRefine.value = true
                    }
                    is Result.Error -> {
                        networkStateRefine.value = false
                    }
                    is Result.Success -> {
                        networkStateRefine.value = false
                    }
                }
            }
        }
    }

}
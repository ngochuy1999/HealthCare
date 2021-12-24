package com.ptithcm.healthcare

import com.ptithcm.healthcare.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { WishListViewModel(get()) }
    viewModel { AuthenticateViewModel(get())}
    viewModel { UserViewModel(get())}
    viewModel { HomeViewModel(get())}
    viewModel { ProductFilterViewModel(get())}
    viewModel { PaymentViewModel(get()) }
    viewModel { CarouselDetailViewModel(get()) }
    viewModel { DesignerViewModel(get()) }
    viewModel { RefineViewModel() }
    viewModel { BrandsViewModel(get()) }
    viewModel { MedicalBillViewModel(get()) }
    viewModel { CheckoutViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { ShareDataViewModel(get(), get()) }
    viewModel { AddProductViewModel(get()) }
    viewModel { UploadViewModel(get()) }
    viewModel { ListenerViewModel() }
    viewModel { ProvidersViewModel(get()) }
    viewModel { QuestionsViewModel(get()) }
    viewModel { RatingViewModel(get()) }
}
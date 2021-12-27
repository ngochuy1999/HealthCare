package com.ptithcm.healthcare

import com.ptithcm.healthcare.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { WishListViewModel(get()) }
    viewModel { AuthenticateViewModel(get())}
    viewModel { UserViewModel(get())}
    viewModel { HomeViewModel(get())}
    viewModel { PaymentViewModel(get()) }
    viewModel { CarouselDetailViewModel(get()) }
    viewModel { RefineViewModel() }
    viewModel { MedicalBillViewModel(get()) }
    viewModel { CheckoutViewModel(get()) }
    viewModel { ListenerViewModel() }
    viewModel { QuestionsViewModel(get()) }
    viewModel { RatingViewModel(get()) }
}
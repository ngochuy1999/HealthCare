package com.ptithcm.core.di

import com.ptithcm.core.repository.*
import com.ptithcm.core.repository.impl.*
import org.koin.dsl.module

val repositoryModule = module {
    single<WishListRepository> { WishListRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<CarouselDetailRepository> { CarouselDetailRepositoryImpl(get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single<CheckoutRepository> { CheckoutRepositoryImpl(get(), get()) }
    single<MedicalBillRepository> { MedicalBillRepositoryImpl(get(), get()) }
    single<QuestionRepository> { QuestionRepositoryImpl(get()) }
    single<RatingRepository> { RatingRepositoryImpl(get()) }
}
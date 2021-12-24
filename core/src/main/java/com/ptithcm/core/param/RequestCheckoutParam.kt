package com.ptithcm.core.param

data class RequestCheckoutParam(
    val billId: Int,
    val description: String,
    val amount: Int,
    val currency: Currency,
    val stripeToken: String)
{
    enum class Currency {
        EUR, USD;
    }

}
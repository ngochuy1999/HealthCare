package com.ptithcm.healthcare.ext

import com.ptithcm.core.model.*
import com.ptithcm.core.param.CategoriesRefine
import com.ptithcm.core.param.ProductsParam
import com.ptithcm.healthcare.constant.COLOR_OPTION
import com.ptithcm.healthcare.constant.SIZE_OPTION
import com.ptithcm.healthcare.ext.refine.getItemCategory

fun ArrayList<SupportedCurrency>.convertArrayListToArray(): Array<String?> {
    val nameCurrencies: ArrayList<String> = arrayListOf()
    this.forEach { item ->
        nameCurrencies.add("${item.name?.toValue()}")
    }
    val array = arrayOfNulls<String>(this.size)
    nameCurrencies.toArray(array)
    return array
}
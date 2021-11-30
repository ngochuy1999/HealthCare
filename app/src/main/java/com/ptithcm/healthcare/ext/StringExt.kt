package com.ptithcm.healthcare.ext

import com.ptithcm.healthcare.constant.KEY_EMPTY
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.deAccent(): String {
    val nfdNormalizedString = Normalizer.normalize(this, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(nfdNormalizedString).replaceAll(KEY_EMPTY)
}

fun String.equalExt(text : String?): Boolean{
    return this.substring(0, 1).deAccent().lowercase(Locale.getDefault()) == text?.substring(0, 1)?.deAccent()
        ?.lowercase(Locale.getDefault())
}

fun String.roundPrice(locale: Locale?): String {
    val df = DecimalFormat("#.##")
    val num = df.format(this.replace(",",".").toDouble()).replace(",",".").toDouble()
    val numberFormat = NumberFormat.getCurrencyInstance()
    numberFormat.apply {
        currency = Currency.getInstance(locale)
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return numberFormat.format(num)
}



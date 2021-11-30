package com.ptithcm.healthcare.util

import java.util.*
import java.util.regex.Pattern

fun checkPhonevalid(phone: String): Boolean {
    val expression = "(09|01|02|03|04|05|06|07|08)+([0-9]{7,11})\\b"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(phone)
    return matcher.matches()
}

fun checkIdentityValid(identity: String): Boolean {
    val expression = "([0-9]{9}|[0-9]{11})\\b"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(identity)
    return matcher.matches()
}

fun checkValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getCurrentDateInMills() : Long{
    return Calendar.getInstance().timeInMillis
}
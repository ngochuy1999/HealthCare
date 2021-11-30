package com.ptithcm.core.model


data class MedicalBill(
    val billId: Int,
    val date: String,
    val examinationFee: Int,
    val medicalBillStatus: MedicalBillStatus,
    val medicalObject: MedicalObject,
    val patient: Profile,
    val waitingTime: String
)
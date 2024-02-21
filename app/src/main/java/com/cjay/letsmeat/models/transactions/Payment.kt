package com.cjay.letsmeat.models.transactions

import android.os.Parcelable
import com.cjay.letsmeat.utils.generateRandomNumbers
import kotlinx.parcelize.Parcelize
import java.util.Date

enum class PaymentStatus {
   PAID ,UNPAID
}

enum class PaymentTypes {
    COD,
    GCASH,
    PAY_IN_COUNTER,
}

@Parcelize
data class Payment(
    val id: String = generateRandomNumbers(),
    val total: Double =0.00,
    var status: PaymentStatus = PaymentStatus.UNPAID,
    val type: PaymentTypes= PaymentTypes.COD,
    var receipt: String = "",
    var updatedAt: Date = Date(),
    val createdAt: Date = Date()
) : Parcelable

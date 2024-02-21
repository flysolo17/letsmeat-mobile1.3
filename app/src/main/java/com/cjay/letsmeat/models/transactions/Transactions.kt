package com.cjay.letsmeat.models.transactions

import android.os.Parcelable
import com.cjay.letsmeat.models.customers.Addresses
import com.cjay.letsmeat.utils.generateRandomNumbers
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Transactions(
    val id: String = generateRandomNumbers(),
    val customerID: String = "",
    val employeeID: String = "",
    val driverID: String = "",
    val type: TransactionType = TransactionType.DELIVERY,
    val address: Addresses? = null,
    val items: List<OrderItems> = emptyList(),
    val shipping: Shipping? = null,
    val payment: Payment ? = Payment(),
    val status: TransactionStatus = TransactionStatus.PENDING,
    val details: List<TransactionDetails> = emptyList(),
    val updatedAt: Date = Date(),
    val transactionDate: Date = Date()
) : Parcelable


fun Transactions.getTotalOrder() : Double {
    val shipping = this.shipping?.total ?: 0.00
    val orderTotal = this.items.sumOf {it.getTotal() }
    return orderTotal.plus(shipping)
}

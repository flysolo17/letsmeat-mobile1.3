package com.cjay.letsmeat.models.transactions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class TransactionDetails(
    val title : String = "",
    val description : String = "",
    val createdAt : Date  =Date()
) : Parcelable


fun Date.isToday(): Boolean {
    val today = Date()
    val formatter = java.text.SimpleDateFormat("yyyyMMdd")
    return formatter.format(this) == formatter.format(today)
}
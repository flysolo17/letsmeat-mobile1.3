package com.cjay.letsmeat.models.transactions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

import java.util.*
@Parcelize
data class Shipping(
    val weight: Double = 0.00,
    val dateFrom: Date = Date(),
    val dateTo: Date = Calendar.getInstance().apply { time = Date(); add(Calendar.DAY_OF_MONTH, 2) }.time,
    val total: Double = 0.00
) : Parcelable

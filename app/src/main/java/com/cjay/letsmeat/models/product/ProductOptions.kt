package com.cjay.letsmeat.models.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductOptions(
    val name: String?  = "",
    val quantity: Int = 0,
    val discount: Double = 0.00
) : Parcelable

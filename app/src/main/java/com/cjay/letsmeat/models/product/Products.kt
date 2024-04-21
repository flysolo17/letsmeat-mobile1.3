package com.cjay.letsmeat.models.product

import android.os.Parcelable
import com.cjay.letsmeat.models.transactions.OrderItems
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Products(val id: String? = "",
                    val image: String? = "",
                    val name: String?= "",
                    val brand: String ?= "",
                    val weight: String?= "",
                    val cost: Double = 0.00,
                    val price: Double = 0.00,
                    val stocks: Int = 0,
                    val options: List<ProductOptions> = mutableListOf(),
                    val description: String? = "",
                    val details: String? = "",
                    val expiration: Date? = Date(),
                    val comments: List<String> = mutableListOf(),
                    val updatedAt: Date  ? = null,
                    val createdAt: Date ? = null,
    ) : Parcelable



fun Products.getName(option: ProductOptions?) : String {
    return if (option !== null) {
        "${option.name} ${this.name}"
    } else {
        this.name ?: ""
    }



}


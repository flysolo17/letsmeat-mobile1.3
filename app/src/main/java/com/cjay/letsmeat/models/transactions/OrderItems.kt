package com.cjay.letsmeat.models.transactions

import android.os.Parcelable
import com.cjay.letsmeat.models.product.ProductOptions
import com.cjay.letsmeat.utils.computeItemSubtotal
import com.cjay.letsmeat.utils.computeItemTotalCost
import com.cjay.letsmeat.utils.generateRandomNumbers
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode

@Parcelize
data class OrderItems(
    val id: String = generateRandomNumbers(),
    val productID : String = " ",
    val name: String = "",
    val image: String  = "",
    val quantity: Int = 1,
    val price : Double  = 0.00,
    val options : ProductOptions? = null,
    val weight : Double = 0.00,
    val cost : Double = 0.00,
    val subtotal : Double= 0.00
) : Parcelable

fun OrderItems.getTotal() : Double {
    return computeItemSubtotal(this.price,this.quantity,this.options)
}

fun OrderItems.getCost() : Double {
    return  computeItemTotalCost(this.cost,this.quantity,this.options)
}

fun List<OrderItems>.getTotalWithoutTax(total : Double) : Double {
    return (100-12) * total / 100;
}
fun List<OrderItems>.getTotalWithTax() : Double {
   return this.sumOf { it.getTotal() }
}
fun List<OrderItems>.computeTotalTax() : Double {
    return 12 * this.getTotalWithTax() / 100
}


fun List<OrderItems>.getTotalWeight(): Double {
    var total = 0.0
    this.forEach {
        total += it.weight
    }


    return BigDecimal(total).setScale(2, RoundingMode.HALF_UP).toDouble()
}


fun OrderItems.getQuantity() : Int {
    val optionQ = this.options?.quantity ?: 1
    return  this.quantity * optionQ
}

fun List<OrderItems>.computeProductSold(productID: String): Int {
    // Sum the quantity of items sold for the given product ID
    return this.sumOf { orderItem ->
        if (orderItem.productID == productID) {
            // Return the quantity of the item if the product ID matches
            orderItem.getQuantity()
        } else {
            // Return 0 if the product ID does not match
            0
        }
    }
}

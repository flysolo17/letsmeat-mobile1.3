package com.cjay.letsmeat.models.cart

import com.cjay.letsmeat.models.product.ProductOptions
import com.cjay.letsmeat.utils.generateRandomNumbers
import java.util.Date

data class Cart(
    val id : String ? = generateRandomNumbers(),
    val userID : String ? = null,
    val productID : String ? = null,
    val option : ProductOptions? = null,
    var quantity : Int = 1,
    val addedAt : Date = Date(),
)

fun Cart.getQuantity() : Int {
    val optionQ = this.option?.quantity ?: 1
    return quantity * optionQ
}

fun Cart.getNextQuantity() : Int {
    val optionQ = this.option?.quantity ?: 1
    return (quantity  + 1) * optionQ
}

fun Cart.getCartTotal() {

}
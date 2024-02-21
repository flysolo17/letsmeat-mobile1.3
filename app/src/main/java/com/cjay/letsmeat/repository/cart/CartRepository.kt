package com.cjay.letsmeat.repository.cart

import android.net.Uri


import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.utils.UiState


interface CartRepository {
    fun addToCart(cart: Cart, result : (UiState<String>) -> Unit)
    fun removeToCart(cartID : String,result: (UiState<String>) -> Unit)
    fun getAllCart(uid : String ,result : (UiState<List<Cart>>) -> Unit)
    fun editToCheckOut(isCheck : Boolean,cart: Cart)
    fun addCartQuantity(cartID: String,result: (UiState<String>) -> Unit)
    fun decreaseCartQuantity(cartID : String ,result: (UiState<String>) -> Unit)

}
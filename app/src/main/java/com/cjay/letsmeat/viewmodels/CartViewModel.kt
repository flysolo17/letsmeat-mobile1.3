package com.cjay.letsmeat.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.repository.cart.CartRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor( val cartRepository: CartRepository): ViewModel() {
    private val cartList = MutableLiveData<UiState<List<Cart>>>()
    val carts : LiveData<UiState<List<Cart>>> get() = cartList

    fun getAllProductsInCart(uid : String) {
        cartRepository.getAllCart(uid) {
            cartList.value = it
        }
    }
}
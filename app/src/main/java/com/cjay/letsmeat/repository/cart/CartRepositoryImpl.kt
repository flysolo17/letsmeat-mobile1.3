package com.cjay.letsmeat.repository.cart


import android.util.Log
import android.widget.Toast
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.repository.cart.CartRepository
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.firestore.FieldValue

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

const val CART_COLLECTION = "Cart"

class CartRepositoryImpl(private val firestore: FirebaseFirestore) :
    CartRepository {
    override fun addToCart(cart: Cart, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(CART_COLLECTION)
            .document(cart.id ?: "")
            .set(cart)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Added!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun removeToCart( cartID: String,result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(CART_COLLECTION)
            .document(cartID)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Deleted!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun getAllCart(uid: String, result: (UiState<List<Cart>>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(CART_COLLECTION)
            .whereEqualTo("userID",uid)
            .orderBy("addedAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.d(CART_COLLECTION,it.message.toString())
                    result.invoke(UiState.FAILED(it.message.toString()))
                }
                value?.let {
                    result.invoke(UiState.SUCCESS(it.toObjects(Cart::class.java)))
                }
            }
    }

    override fun editToCheckOut(isCheck: Boolean, cart: Cart) {
        TODO("Not yet implemented")
    }

    override fun addCartQuantity(cartID: String, result: (UiState<String>) -> Unit) {
        firestore.collection(CART_COLLECTION)
            .document(cartID)
            .update("quantity",FieldValue.increment(1))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Added!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }

    }

    override fun decreaseCartQuantity(cartID: String, result: (UiState<String>) -> Unit) {
        firestore.collection(CART_COLLECTION)
            .document(cartID)
            .update("quantity",FieldValue.increment(-1))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully remove!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }


}
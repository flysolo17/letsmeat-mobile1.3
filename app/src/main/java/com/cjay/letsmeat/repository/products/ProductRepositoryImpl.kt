package com.cjay.letsmeat.repository.products

import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

const val PRODUCT_COLLECTION = "Products";

class ProductRepositoryImpl(private val firestore: FirebaseFirestore) : ProductRepository {
    override fun getAllProducts(result: (UiState<List<Products>>) -> Unit) {
        firestore.collection(PRODUCT_COLLECTION)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.FAILED(it.message.toString()))
                }
                value?.let {
                    result.invoke(UiState.SUCCESS(it.toObjects(Products::class.java)))
                }
            }

    }


}
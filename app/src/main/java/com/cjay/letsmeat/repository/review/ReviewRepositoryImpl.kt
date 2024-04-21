package com.cjay.letsmeat.repository.review

import android.util.Log
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ReviewRepositoryImpl(private val firestore: FirebaseFirestore) : ReviewRepository {
    override suspend fun createReview(reviews: Reviews, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(COLLECTION_NAME)
            .document(reviews.id)
            .set(reviews)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully reviewed"))
                } else {
                    result.invoke(UiState.FAILED(it.exception?.message.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }

    }

    override  suspend  fun getAllReviewsByItemId(itemID: String, result: (UiState<List<Reviews>>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("itemID",itemID)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    result.invoke(UiState.SUCCESS(it.toObjects(Reviews::class.java)))
                }
                error?.let { result.invoke(UiState.FAILED(it.message.toString())) }
            }
    }

    override suspend fun editReview(reviewID: String, result: (UiState<List<String>>) -> Unit) {
        TODO("Not yet implemented")
    }

    override  suspend  fun getReviewByTransactionId(
        transactionID: String,
        result: (UiState<List<Reviews>>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("transactionID",transactionID)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                   result.invoke(UiState.SUCCESS(it.result.toObjects(Reviews::class.java)))

                }
            }.addOnFailureListener {
                Log.d(COLLECTION_NAME,it.message.toString())
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }
    companion object {
        const val COLLECTION_NAME = "Reviews"
    }
}
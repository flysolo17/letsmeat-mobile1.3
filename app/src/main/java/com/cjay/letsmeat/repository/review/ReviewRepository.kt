package com.cjay.letsmeat.repository.review

import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.utils.UiState

interface ReviewRepository {

    suspend fun createReview(reviews: Reviews,result: (UiState<String>) -> Unit)

  suspend  fun getAllReviewsByItemId(itemID : String,result : (UiState<List<Reviews>>) -> Unit)
   suspend fun editReview(reviewID : String,result: (UiState<List<String>>) -> Unit)

   suspend fun getReviewByTransactionId(transactionID: String,result: (UiState<List<Reviews>>) -> Unit)
}
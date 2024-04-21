package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.repository.accounts.AccountsRepository
import com.cjay.letsmeat.repository.review.ReviewRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RatingViewModel @Inject constructor(private val reviewRepository: ReviewRepository): ViewModel() {

    fun createReview(reviews: Reviews,result : (UiState<String>) -> Unit) {
        viewModelScope.launch {
            reviewRepository.createReview(reviews, result)
        }
    }

    fun getReviewByTransactionId(transactionId : String,result: (UiState<List<Reviews>>) -> Unit) {
        viewModelScope.launch {
            reviewRepository.getReviewByTransactionId(transactionId,result)
        }
    }

    fun getComments(productID : String,result: (UiState<List<Reviews>>) -> Unit) {
        viewModelScope.launch {
            reviewRepository.getAllReviewsByItemId(productID,result)
        }
    }


}
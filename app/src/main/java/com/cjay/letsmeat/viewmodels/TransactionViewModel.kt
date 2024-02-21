package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.repository.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TransactionViewModel @Inject constructor(val transactionRepository: TransactionRepository): ViewModel() {
   private val transactionList = MutableLiveData<List<Transactions>>()

    val transactions : LiveData<List<Transactions>>get() = transactionList
    fun setTransactionList(transactions: List<Transactions>) {
        transactionList.value = transactions
    }
}
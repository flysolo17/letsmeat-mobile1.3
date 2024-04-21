package com.cjay.letsmeat.repository.transaction

import android.net.Uri
import com.cjay.letsmeat.models.transactions.Payment
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.utils.UiState

interface TransactionRepository {

    fun createTransaction(transactions: Transactions,result : (UiState<String>) ->Unit)
    fun getAllTransactionByCustomerID(cid : String,result: (UiState<List<Transactions>>) -> Unit)
    fun addPayment(transactionID : String,payment: Payment,result: (UiState<String>) -> Unit)
    suspend fun uploadReceipt(uri: Uri,result: (UiState<String>) -> Unit)
    fun cancelTransaction(transactionID: String,result: (UiState<String>) -> Unit)

    fun getDriverInfo(driverID : String ,result: (UiState<String>) -> Unit)

}
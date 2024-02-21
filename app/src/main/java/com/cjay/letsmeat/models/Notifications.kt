package com.cjay.letsmeat.models

import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.Transactions

data class Notifications(
    val transactions: Transactions,
    val transactionDetails: TransactionDetails
)

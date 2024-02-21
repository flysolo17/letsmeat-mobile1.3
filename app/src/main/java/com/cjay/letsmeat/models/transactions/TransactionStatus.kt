package com.cjay.letsmeat.models.transactions

enum class TransactionStatus {
    PENDING,
    ACCEPTED,
    TO_SHIP,
    TO_PICK_UP,
    TO_PACKED,
    TO_RECEIVE,
    COMPLETED,
    DECLINED,
    CANCELED
}


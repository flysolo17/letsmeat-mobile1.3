package com.cjay.letsmeat.models.messages

import java.util.Date
import java.util.UUID

data class Messages(
    val id : String = UUID.randomUUID().toString(),
    val senderID : String = "",
    val receiverID : String = "",
    val message : String = "",
    val createdAt : Date = Date(),
)

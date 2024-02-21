package com.cjay.letsmeat.repository.messages

import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.models.messages.Messages


interface MessagesRepository {
    fun sendMessage(messages: Messages, result : (UiState<String>) -> Unit)
    fun getAllMyMessage(uid : String , result: (UiState<List<Messages>>) -> Unit)
}
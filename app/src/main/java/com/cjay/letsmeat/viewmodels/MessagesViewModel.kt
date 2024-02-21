package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjay.letsmeat.models.messages.Messages
import com.cjay.letsmeat.repository.messages.MessagesRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MessagesViewModel @Inject constructor( val messagesRepository: MessagesRepository)  : ViewModel() {
    private val _messages = MutableLiveData<UiState<List<Messages>>>()
    val messages : LiveData<UiState<List<Messages>>> get() = _messages


    fun getAllMessages(uid : String) {
        messagesRepository.getAllMyMessage(uid) {
            _messages.value = it
        }
    }

}
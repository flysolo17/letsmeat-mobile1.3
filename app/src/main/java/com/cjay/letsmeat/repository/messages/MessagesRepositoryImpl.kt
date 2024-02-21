package com.cjay.letsmeat.repository.messages

import android.util.Log
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.cjay.letsmeat.models.messages.Messages

const val MESSAGES_COLLECTION = "Messages"
class MessagesRepositoryImpl(private val firestore: FirebaseFirestore): MessagesRepository {
    override fun sendMessage(messages: Messages, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(MESSAGES_COLLECTION)
            .add(messages)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("message sent!"))
                } else {
                    result.invoke(UiState.SUCCESS("message failed to sent!"))
                }
            }
    }

    override fun getAllMyMessage(uid : String,result: (UiState<List<Messages>>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(MESSAGES_COLLECTION)
            .where(Filter.or(
                Filter.equalTo("senderID",uid),
                Filter.equalTo("receiverID",uid))
            ).orderBy(
                "createdAt",
                Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.FAILED(it.message.toString()))
                    Log.d("messages",it.message.toString())
                }
                value?.let {
                    result.invoke(UiState.SUCCESS(it.toObjects(Messages::class.java)))
                }
            }
    }
}
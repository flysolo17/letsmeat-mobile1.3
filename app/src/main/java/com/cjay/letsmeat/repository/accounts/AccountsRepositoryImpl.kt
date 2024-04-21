package com.cjay.letsmeat.repository.accounts

import com.cjay.letsmeat.models.users.Drivers
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore

class AccountsRepositoryImpl(private val firestore: FirebaseFirestore) : AccountsRepository {
    override fun getDriverInfo(driverID: String, result: (UiState<Drivers>) -> Unit) {
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id",driverID)
            .limit(1)
            .get()
            .addOnSuccessListener {
                val results = it.toObjects(Drivers::class.java)
                if (results.isNotEmpty()) {
                    result.invoke(UiState.SUCCESS(results[0]))
                } else {
                    result.invoke(UiState.FAILED("Unknown Driver"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED("Unknown Driver"))
            }
    }
    companion object {
        const val   COLLECTION_NAME = "Users";
    }
}
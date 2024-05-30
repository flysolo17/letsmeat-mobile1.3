package com.cjay.letsmeat.repository.transaction

import android.net.Uri
import android.util.Log
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.models.transactions.Payment
import com.cjay.letsmeat.models.transactions.PaymentStatus
import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.repository.auth.USER_COLLECTION
import com.cjay.letsmeat.repository.cart.CART_COLLECTION
import com.cjay.letsmeat.repository.products.PRODUCT_COLLECTION
import com.cjay.letsmeat.utils.UiState
import com.google.common.io.Files
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

const val TRANSACTION_COLLECTIONS = "Transactions"
const val PAYMENT_STORAGE = "Payments"
class TransactionRepositoryImpl(private val firestore: FirebaseFirestore,private val storage: FirebaseStorage) : TransactionRepository {
    override fun createTransaction(transactions: Transactions, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(TRANSACTION_COLLECTIONS)
            .document(transactions.id)
            .set(transactions)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val batch = firestore.batch()
                    transactions.items.forEach {
                        val optionQuantity = it.options?.quantity ?: 1
                        val quantity = it.quantity * optionQuantity
                        val ref =  firestore.collection(PRODUCT_COLLECTION).document(it.productID)
                        batch.update(ref,"stocks",FieldValue.increment(-quantity.toDouble()))
                    }
                    batch.commit().addOnCompleteListener {
                        result.invoke(UiState.SUCCESS("Successfully ordered!"))
                    }.addOnFailureListener {
                        result.invoke(UiState.FAILED(it.message.toString()))
                    }

                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun getAllTransactionByCustomerID(
        cid: String,
        result: (UiState<List<Transactions>>) -> Unit
    ) {
        firestore.collection(TRANSACTION_COLLECTIONS)
            .whereEqualTo("customerID",cid)
            .orderBy("transactionDate", Query.Direction.DESCENDING)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.d(TRANSACTION_COLLECTIONS,it.message.toString())
                    result.invoke(UiState.FAILED(it.message.toString()))
                }
                value?.let {
                    result.invoke(UiState.SUCCESS(it.toObjects(Transactions::class.java)))
                }
            }
    }

    override fun addPayment(transactionID : String ,payment: Payment, result: (UiState<String>) -> Unit) {

        firestore.collection(TRANSACTION_COLLECTIONS)
            .document(transactionID)
            .update(
                "updatedAt",Date(),
                "payment",payment,
                "details", FieldValue.arrayUnion(TransactionDetails("Payment","Payment is now complete!")))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully paid!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override suspend fun uploadReceipt(uri: Uri, result: (UiState<String>) -> Unit) {
        val storage  = storage.reference.child(PAYMENT_STORAGE).child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(uri.toString()))
        result.invoke(UiState.LOADING)
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storage
                    .putFile(uri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            result.invoke(UiState.SUCCESS(uri.toString()))
        } catch (e: FirebaseException){
            result.invoke(UiState.FAILED(e.message!!))
        }catch (e: Exception){
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override fun cancelTransaction(transactionID: String, result: (UiState<String>) -> Unit) {
        val details = TransactionDetails(title = "Cencelled", description = "Order cancelled")
        firestore.collection(TRANSACTION_COLLECTIONS)
            .document(transactionID)
            .update(
                "status",TransactionStatus.CANCELED,
                "details",FieldValue.arrayUnion(details)
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully cancelled!"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }

    }

    override fun getDriverInfo(driverID: String, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}
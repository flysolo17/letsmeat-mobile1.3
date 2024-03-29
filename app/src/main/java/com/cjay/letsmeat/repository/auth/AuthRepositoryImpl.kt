package com.cjay.letsmeat.repository.auth


import android.net.Uri
import com.cjay.letsmeat.models.customers.Addresses
import com.cjay.letsmeat.models.customers.CustomerType
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.UiState
import com.google.android.gms.tasks.Task
import com.google.common.io.Files
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val USER_COLLECTION = "Customers";
class AuthRepositoryImpl(private  val firestore : FirebaseFirestore,private  val auth : FirebaseAuth,private val storage: FirebaseStorage) :
    AuthRepository {

    override fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Logged in"))
                } else {
                    result.invoke(UiState.FAILED(it.exception?.message.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun verifyOTP(
        verificationCode: String,
        OTP: String,
        result: (UiState<FirebaseUser>) -> Unit
    ) {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, OTP)
        result.invoke(UiState.LOADING)
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user: FirebaseUser = auth.currentUser!!
                    result.invoke(UiState.SUCCESS(user))
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.FAILED("Invalid Credential!"))
                    } else {
                        result.invoke(UiState.FAILED("Auth Failed! "))
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED("Authentication Failed:  ${it.message}"))
            }
    }


    override fun getAccountByID(uid: String, result: (UiState<Customers?>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(USER_COLLECTION)
            .document(uid)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.FAILED(it.message.toString()))
                }
                value?.let {
                    val customer = value.toObject(Customers::class.java)
                    result.invoke(UiState.SUCCESS(customer))

                }
            }
    }
    override fun checkUserByID(currentUser: FirebaseUser, result: (UiState<Customers>) -> Unit) {
        result.invoke(UiState.LOADING)

        val userDocumentRef = firestore.collection(USER_COLLECTION).document(currentUser.uid)

        userDocumentRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val customers = documentSnapshot.toObject(Customers::class.java)
                    customers?.let { c ->
                        result.invoke(UiState.SUCCESS(c))
                    }
                } else {
                    val newDocument = Customers(
                        currentUser.uid, "", currentUser.phoneNumber ?: "", "No name",
                        CustomerType.REGULAR, emptyList(), 0
                    )
                    userDocumentRef.set(newDocument).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            result.invoke(UiState.SUCCESS(newDocument))
                        } else {
                            result.invoke(UiState.FAILED(updateTask.exception?.toString() ?: "Unknown error"))
                        }
                    }.addOnFailureListener {
                        result.invoke(UiState.FAILED(it.message.toString()))
                    }
                }
            } else {
                result.invoke(UiState.FAILED(task.exception?.toString() ?: "Unknown error"))
            }
        }
    }








    override fun updateFullname(uid: String, fullname: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(USER_COLLECTION)
            .document(uid)
            .update("name",fullname)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Updated!"))
                } else {
                    result.invoke(UiState.FAILED("unknown error!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun createAddress(uid: String, addresses: Addresses, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.LOADING)
        firestore.collection(USER_COLLECTION)
            .document(uid)
            .update("addresses",FieldValue.arrayUnion(addresses))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Added!"))
                } else {
                    result.invoke(UiState.FAILED("Failed creating address!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }

    }

    override fun changeDefaultAddress(
        uid: String,
        position: Int,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        firestore.collection(USER_COLLECTION)
            .document(uid)
            .update("defaultAddress",position)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Default address Change successfully"))
                } else {
                    result.invoke(UiState.FAILED("error"))
                }
            }
    }

    override fun updateAccount(customers: Customers, result: (UiState<String>) -> Unit) {
        firestore.collection(USER_COLLECTION)
            .document(customers.id ?: "")
            .set(customers)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.SUCCESS("Successfully Updated"))
                } else {
                    result.invoke(UiState.FAILED(it.exception.toString()))
                }
            }.addOnFailureListener {
                result.invoke(UiState.FAILED(it.message.toString()))
            }
    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun uploadProfile(imageUri: Uri, uid: String, result: (UiState<Uri>) -> Unit) {
        val storage  = storage.reference.child(USER_COLLECTION).child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(imageUri.toString()))
        result.invoke(UiState.LOADING)
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storage
                    .putFile(imageUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            result.invoke(UiState.SUCCESS(uri))
        } catch (e: FirebaseException){
            result.invoke(UiState.FAILED(e.message!!))
        }catch (e: Exception){
            result.invoke(UiState.FAILED(e.message!!))
        }
    }


}
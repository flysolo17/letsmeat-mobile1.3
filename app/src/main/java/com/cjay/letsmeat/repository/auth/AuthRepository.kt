package com.cjay.letsmeat.repository.auth

import android.net.Uri
import com.cjay.letsmeat.models.customers.Addresses
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.UiState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential


interface AuthRepository {

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential,result: (UiState<String>) -> Unit)
    fun verifyOTP(verificationCode: String,OTP: String,result: (UiState<FirebaseUser>) -> Unit)
    suspend  fun getAccountByID(uid : String,result: (UiState<Customers?>) -> Unit)
       fun checkUserByID(currentUser: FirebaseUser,result: (UiState<Customers>) -> Unit)
    fun updateFullname(uid: String  ,fullname : String,result: (UiState<String>) -> Unit)
    fun createAddress(uid: String, addresses: Addresses, result: (UiState<String>) -> Unit)
    fun changeDefaultAddress(uid: String,position : Int,result: (UiState<String>) -> Unit)
    fun updateAccount(customers: Customers,result: (UiState<String>) -> Unit)
    fun logout()
    suspend fun uploadProfile(imageUri: Uri,uid: String,result: (UiState<Uri>) -> Unit)
}
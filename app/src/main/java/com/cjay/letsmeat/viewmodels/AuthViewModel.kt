package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.repository.auth.AuthRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(  val authRepository: AuthRepository): ViewModel() {

    val _customers = MutableLiveData<UiState<Customers?>>(null)
    val customers : LiveData<UiState<Customers?>> get() = _customers


    fun getUserByID(uid : String) {
        authRepository.getAccountByID(uid) {
            _customers.value = it
        }
    }

}
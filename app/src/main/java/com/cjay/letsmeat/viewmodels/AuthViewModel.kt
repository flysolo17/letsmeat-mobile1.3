package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.repository.auth.AuthRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(  val authRepository: AuthRepository): ViewModel() {

    val _customers = MutableLiveData<UiState<Customers?>>(null)
    val customers : LiveData<UiState<Customers?>> get() = _customers


    fun getUserByID(uid : String) {
        viewModelScope.launch {
            authRepository.getAccountByID(uid) {
                _customers.value = it
            }
        }
    }

    fun getCustomerByID(uid : String,result : (UiState<Customers?> )-> Unit) {
        viewModelScope.launch {
            authRepository.getAccountByID(uid,result)
        }
    }
}
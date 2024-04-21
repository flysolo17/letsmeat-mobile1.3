package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.ViewModel
import com.cjay.letsmeat.repository.accounts.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(val accountsRepository: AccountsRepository)  : ViewModel(){


}
package com.cjay.letsmeat.repository.accounts

import com.cjay.letsmeat.models.users.Drivers
import com.cjay.letsmeat.utils.UiState

interface AccountsRepository {
    fun getDriverInfo(driverID : String,result : (UiState<Drivers>) -> Unit)
}
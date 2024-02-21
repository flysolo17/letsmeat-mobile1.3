package com.cjay.letsmeat.models.customers

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneAuth(
    val phone : String? = null,
    val forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null,
    val code : String? = null,
) : Parcelable

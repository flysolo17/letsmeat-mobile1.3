package com.cjay.letsmeat.models.customers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contacts(
    val name : String? = null,
    val phone : String? = null) : Parcelable

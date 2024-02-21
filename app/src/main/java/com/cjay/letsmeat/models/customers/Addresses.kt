package com.cjay.letsmeat.models.customers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Addresses(  val contacts: Contacts ? = null,
                       val addressLine : String ? = null,
                       val postalCode : Int ? = null ,
                       val street : String? = null) : Parcelable

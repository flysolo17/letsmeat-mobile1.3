package com.cjay.letsmeat.models.customers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Customers(val id : String ? = null,
                     var profile : String= "",
                     val phone : String ="",
                     var fullname : String  = "",
                     val type : CustomerType  = CustomerType.REGULAR,
                     val addresses : List<Addresses>  = mutableListOf(),
                     val defaultAddress : Int  = 0,) : Parcelable


fun Customers.getDefaultAddress() :Addresses ?{
    if (this.addresses.isNotEmpty()) {
        return this.addresses[this.defaultAddress]
    }
    return null
}
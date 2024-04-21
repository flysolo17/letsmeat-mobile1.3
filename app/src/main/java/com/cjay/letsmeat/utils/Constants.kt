package com.cjay.letsmeat.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.cjay.letsmeat.models.messages.Messages
import com.cjay.letsmeat.models.product.ProductOptions
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.models.transactions.Shipping

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

fun Context.getImageTypeFromUri(imageUri: Uri?): String? {
    val contentResolver: ContentResolver = contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri!!))
}



fun Double.format(): String {
    return String.format("%.2f", this)
}

fun formatPrice(num : Double) : String {
    return "₱ ${num.format()}"
}

fun generateRandomNumber(repeat : Int): String {
    // Generate a random 10-digit number
    val randomNumber = buildString {
        repeat(repeat) {
            append(Random.nextInt(0,10))
        }
    }
    return randomNumber
}


fun Double.toPHP(): String {
    val formattedString = String.format("₱ %,.2f", this)
    return formattedString
}





fun Date.timeAgoOrDateTimeFormat(): String {
    val now = Date()
    val timeDifference = now.time - this.time
    val minutesInMillis = 60 * 1000
    val hoursInMillis = 60 * minutesInMillis
    val daysInMillis = 24 * hoursInMillis

    return when {
        timeDifference < minutesInMillis -> "just now"
        timeDifference < hoursInMillis -> "${timeDifference / minutesInMillis} minute${if (timeDifference / minutesInMillis > 1) "s" else ""} ago"
        timeDifference < daysInMillis -> "${timeDifference / hoursInMillis} hour${if (timeDifference / hoursInMillis > 1) "s" else ""} ago"
        else -> SimpleDateFormat("MMM/dd/yyyy HH:mm", Locale.getDefault()).format(this)
    }
}


fun String.getDigits(): Double {
    val match = Regex("-?\\d+(\\.\\d+)?").find(this)
    Log.d("conversion", match?.value.toString())
    return match?.value?.toDoubleOrNull() ?: 0.00
}


fun String.getStringInput(input: String): String {
    val match = Regex("[a-zA-Z]+").find(input) // Match one or more alphabetic characters
    return match?.value ?: ""
}
fun generateRandomNumbers(length: Int = 15): String {
    require(length >= 0) { "Length must be a non-negative number" }
    val random = Random.Default
    val numbers = "0123456789"

    return (1..length)
        .map { numbers[random.nextInt(numbers.length)] }
        .joinToString("")
}

fun computeItemSubtotal(price: Double, quantity: Int, option: ProductOptions?): Double {
    val discount = option?.discount ?: 0.0
    val optionQuantity = option?.quantity ?: 1
    val totalQuantity = quantity * optionQuantity
    val totalBeforeDiscount = price * totalQuantity
    return totalBeforeDiscount - (totalBeforeDiscount * discount / 100)
}


fun computeItemTotalCost(cost: Double, quantity: Int, option: ProductOptions?): Double {
    val optionQuantity = option?.quantity ?: 1
    val totalQuantity = quantity * optionQuantity
    return cost * totalQuantity
}

/**
 * Convert product weight to kg
 */
fun String.convertToKilogram(): Double {
    val unit = getStringInput(input = this)

    val weight = this.getDigits()
    Log.d("convertion",weight.toString())
    return when (unit.lowercase()) {
        "kg" -> weight
        "g" -> weight / 1000
        "ml" -> weight * 0.001
        "l" -> weight * 1000
        else -> 0.00
    }
}


/**
 * Compute the total order items
 */

fun Double.calculateShippingFee(): Double {
    return when (this) {
        in 0.0..10.0 -> 100.00
        in 10.01..20.0 -> 200.00
        in 20.01..30.0 -> 300.00
        in 30.01..40.0 -> 400.00
        in 40.01..50.0 -> 500.00
        in 50.01..60.0 -> 600.00
        in 60.01..70.0 -> 700.00
        in 70.01..80.0 -> 800.00
        in 80.01..90.0 -> 900.00
        else -> 1000.00
    }
}


fun List<Messages>.getLastMessage(myID : String) : Int {
    var count = 0
    for (message in this) {
        if (message.senderID == myID) {
            break
        }
        count += 1
    }
    return count
}

fun Date.toDateTime(): String {
    val format = SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.getDefault())
    return format.format(this)
}


fun Shipping?.getShippingDate(): String {
    if (this == null) {
        return "NA"
    }

    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
    val fromDate = format.format(this.dateFrom)
    val toDate = format.format(this.dateTo)

    return "$fromDate - $toDate"
}


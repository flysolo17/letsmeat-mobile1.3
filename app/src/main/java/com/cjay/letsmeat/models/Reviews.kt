package com.cjay.letsmeat.models

import android.media.Rating
import android.os.Parcelable
import com.cjay.letsmeat.utils.generateRandomNumbers
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Reviews(
    val id : String  = generateRandomNumbers(15),
    val transactionID : String ? = "",
    val userID : String ? = "",
    val itemID : String ? = "",
    val rating : Float = 0f,
    val comment : String ? = "",
    val replies : List<Replies> = listOf(),
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
) : Parcelable



fun  List<Reviews>.getAveReviews(productID : String) : Float {
    val reviews = this.filter { it.itemID == productID }
    val average  = reviews.sumOf { it.rating.toDouble() } / reviews.size
    return average.toFloat()
}
fun List<Reviews>.getTotalReviews(productID: String): String {
    // Filter reviews based on the product ID and sum the ratings
    val reviews = this.filter { it.itemID == productID }.sumOf { it.rating.toDouble() }

    // If the sum of the ratings is greater than or equal to 1000
    return if (reviews >= 1000) {

        String.format("%.1fk", reviews / 1000)
    } else {

        String.format("%.2f", reviews)
    }
}


@Parcelize
data class Replies(
    val userID: String  = "",
    val reply : String = "",
    val type : ReplyType = ReplyType.STAFF,
    val createdAt: Date = Date()
) : Parcelable

enum class ReplyType {
    STAFF,
    CUSTOMER
}
package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.google.android.material.imageview.ShapeableImageView

class CustomerReviewAdapter(private val context : Context,private val reviews : List<Reviews> ,private val viewmodel : AuthViewModel): RecyclerView.Adapter<CustomerReviewAdapter.CustomerReviewViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_reviews,parent,false)
        return CustomerReviewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: CustomerReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.textCustomerReview.text = review.comment
        holder.ratingBar.rating = review.rating
        holder.displayCustomerInfo(review.userID ?: "",viewmodel)

    }
    class CustomerReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textCustomerName : TextView = itemView.findViewById(R.id.textCustomerName)
        val textCustomerReview : TextView = itemView.findViewById(R.id.textCustomerFeedback)
        private val imageCustomerProfile : ShapeableImageView = itemView.findViewById(R.id.imageCustomerProfile)
        val ratingBar : RatingBar = itemView.findViewById(R.id.customerRating)

        fun displayCustomerInfo(uid : String ,viewmodel: AuthViewModel) {
            viewmodel.getCustomerByID(uid = uid) {
                if(it is UiState.SUCCESS) {
                    Glide.with(itemView.context).load(it.data?.profile).error(R.drawable.profile).into(imageCustomerProfile)
                    textCustomerName.text = it.data?.fullname ?: "anonymous"
                }
            }

        }
    }
}
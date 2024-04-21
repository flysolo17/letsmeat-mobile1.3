package com.cjay.letsmeat.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.utils.toPHP
import com.google.android.material.button.MaterialButton

interface RatingClickListener {
    fun onRateProduct(items: OrderItems)
}
class RatingAdapter(private val context : Context, private val items : List<OrderItems>, val reviews : List<Reviews>,private val ratingClickListener: RatingClickListener) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val  view = LayoutInflater.from(context).inflate(R.layout.adapter_rating,parent,false)
        return RatingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item = items[position]
        holder.textItemName.text = item.name
        holder.textItemPrice.text = item.price.toPHP()
        holder.textItemQuantity.text = "${item.quantity}x"
        if (item.options != null) {
            holder.textProductVariation.text = item.options.name
        } else {
            holder.textProductVariation.visibility = View.GONE
        }

        Glide.with(context)
            .load(item.image)
            .error(R.drawable.product)
            .into(holder.productImage)

        holder.ratingButton.setOnClickListener {
            ratingClickListener.onRateProduct(item)
        }
        val data = reviews.filter { it.itemID == item.id }
        holder.displayReview(data)
    }
    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textItemName =itemView.findViewById<TextView>(R.id.textProductName)
        val textItemPrice =   itemView.findViewById<TextView>(R.id.textPrice)
        val textItemQuantity: TextView = itemView.findViewById<TextView>(R.id.textQuantity)
        val textProductVariation : TextView = itemView.findViewById(R.id.textProductVariation)
        val productImage : ImageView =  itemView.findViewById(R.id.imageProduct)
        val ratingButton : MaterialButton = itemView.findViewById(R.id.btnRate)

        private val textReview : TextView = itemView.findViewById(R.id.textReview)
        private val rating : RatingBar = itemView.findViewById(R.id.rating)

        private val layoutReview : LinearLayout = itemView.findViewById(R.id.layoutReview)

        fun displayReview(reviews: List<Reviews>) {
            if (reviews.isNotEmpty()) {
                layoutReview.visibility = View.VISIBLE
                ratingButton.visibility = View.GONE
                textReview.text = reviews[0].comment
                rating.rating = reviews[0].rating
            }


        }
    }


}
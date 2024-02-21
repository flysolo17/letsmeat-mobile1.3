package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.transactions.PaymentTypes
import com.cjay.letsmeat.models.transactions.TransactionType
import com.cjay.letsmeat.models.transactions.Transactions
import com.google.android.material.card.MaterialCardView


interface DeliveryOptionClickListener {
    fun onDeliveryClicked(position: Int)
}
class DeliveryOptionAdapter(private val context : Context,private val options : List<TransactionType>,private val onDeliveryOptionClickListener: DeliveryOptionClickListener) : RecyclerView.Adapter<DeliveryOptionAdapter.DeliveryOptionViewHolder>(){
    private var selectedDeliveryType = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryOptionViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.list_transaction_type,parent,false)
        return DeliveryOptionViewHolder(view)
    }

    override fun getItemCount(): Int {
       return options.size
    }

    override fun onBindViewHolder(holder: DeliveryOptionViewHolder, position: Int) {
        when (options[position]) {
            TransactionType.DELIVERY -> {
                with(holder) {
                    textPaymentType.text = "Delivery"
                    textPaymentTypeDesc.text = "Delivery to your doorstep"
                    imagePaymentType.setImageResource(R.drawable.delivery)
                }
            }
            else -> {
                with(holder) {
                    textPaymentType.text = "Pick up"
                    textPaymentTypeDesc.text = "Pick up your order in our warehouse"
                    imagePaymentType.setImageResource(R.drawable.pick_up)
                }
            }
        }
        holder.itemView.setOnClickListener {
            onDeliveryOptionClickListener.onDeliveryClicked(holder.adapterPosition)
        }
        if (position == selectedDeliveryType) {
            holder.cardPaymentType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        } else {
            holder.cardPaymentType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun getSelected() : Int {
        return selectedDeliveryType;
    }
    fun setDeliveryOption(position: Int) {
        selectedDeliveryType =position
        notifyDataSetChanged()
    }

    class DeliveryOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textPaymentType : TextView = itemView.findViewById(R.id.textDeliveryType)
        val textPaymentTypeDesc : TextView = itemView.findViewById(R.id.textDeliveryDesc)
        val imagePaymentType : ImageView = itemView.findViewById(R.id.imageDeliveryType)
        val  cardPaymentType : MaterialCardView = itemView.findViewById(R.id.cardType)
    }

}
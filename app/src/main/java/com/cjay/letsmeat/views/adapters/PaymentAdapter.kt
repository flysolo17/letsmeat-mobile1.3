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
import com.google.android.material.card.MaterialCardView


interface PaymentClickListener {
    fun onPaymentClick(position: Int)
}
class PaymentAdapter(private val context : Context,private val methods : List<PaymentTypes>,private val paymentClickListener: PaymentClickListener): RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {
    private var selectedPayment = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.list_payment,parent,false)
        return PaymentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  methods.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        when (methods[position]) {
            PaymentTypes.COD -> {
                with(holder) {
                    textPaymentType.text = "Cash On Delivery"
                    textPaymentTypeDesc.text = "Pay when you receive"
                    imagePaymentType.setImageResource(R.drawable.cod)
                }
            }
            PaymentTypes.PAY_IN_COUNTER -> {
                with(holder) {
                    textPaymentType.text = "Pay in Counter"
                    textPaymentTypeDesc.text = "Pay at JJF store"
                    imagePaymentType.setImageResource(R.drawable.counter)
                }
            }
            else -> {
                with(holder) {
                    textPaymentType.text = "GCASH Payment"
                    textPaymentTypeDesc.text = "Pay with e-wallet"
                    imagePaymentType.setImageResource(R.drawable.gcash)
                }
            }
        }
        holder.itemView.setOnClickListener {
           paymentClickListener.onPaymentClick(holder.adapterPosition)
        }
        if (position == selectedPayment) {
            holder.cardPaymentType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        } else {
            holder.cardPaymentType.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

    }
    fun getSelected() : Int {
        return selectedPayment;
    }
    fun selectPayment(position: Int) {
        selectedPayment = position
        notifyDataSetChanged()
    }
    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textPaymentType : TextView = itemView.findViewById(R.id.textPaymentType)
        val textPaymentTypeDesc : TextView = itemView.findViewById(R.id.textPaymentDesc)
        val imagePaymentType : ImageView = itemView.findViewById(R.id.imagePaymentType)
        val  cardPaymentType : MaterialCardView = itemView.findViewById(R.id.cardPayment)

    }
}
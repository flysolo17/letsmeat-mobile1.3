package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.transactions.TransactionDetails

import java.text.SimpleDateFormat


class TransactionStatusAdapter(val context: Context, private val details : List<TransactionDetails>) : RecyclerView.Adapter<TransactionStatusAdapter.TransactionStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionStatusViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_transaction_status,parent,false)
        return TransactionStatusViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  details.size
    }

    override fun onBindViewHolder(holder: TransactionStatusViewHolder, position: Int) {
        val detail = details[position]
        holder.textStatusTitle.text = detail.title
        holder.textStatusDesc.text = detail.description
        val timeFormatter = SimpleDateFormat("HH:mm a")
        val dateFormatter = SimpleDateFormat("MMM d")
        holder.textStatusTime.text = timeFormatter.format(detail.createdAt)
        holder.textStatusDate.text = dateFormatter.format(detail.createdAt)
    }
    class TransactionStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textStatusTitle : TextView = itemView.findViewById(R.id.textStatusTitle)
        val textStatusDesc : TextView = itemView.findViewById(R.id.textStatusDesc)
        val textStatusTime: TextView = itemView.findViewById(R.id.textStatusTime)
        val textStatusDate: TextView = itemView.findViewById(R.id.textStatusDate)
    }

}
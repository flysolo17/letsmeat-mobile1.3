package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.telecom.Call.Details
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.Notifications
import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.utils.timeAgoOrDateTimeFormat


interface  NotificationClickListener {
    fun onNotificationClicked(transactions: Transactions)
}
class NotificationAdapter(private val context: Context,private val notifications : List<Notifications>,private val notificationClickListener: NotificationClickListener) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(
            context
        ).inflate(R.layout.adapter_notifications,parent,false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notif =  notifications[position]
        holder.textTitle.text = notif.transactionDetails.title
        holder.textDesc.text = notif.transactionDetails.description
        holder.textDate.text = notif.transactionDetails.createdAt.timeAgoOrDateTimeFormat()
        holder.itemView.setOnClickListener {
            notificationClickListener.onNotificationClicked(notif.transactions)
        }
    }
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle : TextView = itemView.findViewById(R.id.textTitle)
        val textDesc : TextView = itemView.findViewById(R.id.textDescription)
        val textDate : TextView = itemView.findViewById(R.id.textDate)
    }
}
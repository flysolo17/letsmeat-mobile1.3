package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.models.transactions.PaymentStatus
import com.cjay.letsmeat.models.transactions.PaymentTypes
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.utils.toPHP

interface TransactionClickListener {
    fun cancelTransaction(transactions: Transactions)
    fun viewTransaction(transactions: Transactions)
    fun payWithGCash(transactions: Transactions)
}
class TransactionAdapter(private val context: Context, private val transactions : List<Transactions>, private val transactionClickListener: TransactionClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_transactions,parent,false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.textTransactionID.text = transaction.id
        holder.textTotal.text = transaction.payment?.total?.toPHP()
        holder.textTransactionStatus.text = transaction.status.name
        holder.buttonCancel.setOnClickListener {
            transactionClickListener.cancelTransaction(transaction)
        }
        holder.buttonPaynow.setOnClickListener {
            transactionClickListener.payWithGCash(transaction)
        }
        transaction.items.map {
            holder.displayItems(it)
        }
        holder.itemView.setOnClickListener {
            transactionClickListener.viewTransaction(transaction)
        }

        if (transaction.status == TransactionStatus.PENDING) {
            holder.buttonCancel.visibility = View.VISIBLE
        } else {
            holder.buttonCancel.visibility = View.GONE
        }

        if (transaction.payment?.type == PaymentTypes.GCASH && transaction.payment.status == PaymentStatus.UNPAID) {
            holder.buttonPaynow.visibility = View.VISIBLE
            holder.textWarning.visibility = View.VISIBLE
        } else {
            holder.textWarning.visibility = View.GONE
        }
    }
    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textTransactionID : TextView = itemView.findViewById(R.id.textTransactionID)
        val textTransactionStatus : TextView = itemView.findViewById(R.id.textStatus)
        val textTotal : TextView = itemView.findViewById(R.id.textTotal)
        private val layoutItems : LinearLayout = itemView.findViewById(R.id.layoutItems)
        val textWarning : TextView = itemView.findViewById(R.id.textWarning)
        val buttonCancel : TextView = itemView.findViewById(R.id.buttonCancel)
        val buttonPaynow : TextView = itemView.findViewById(R.id.buttonPaynow)

        fun displayItems(orderItems: OrderItems) {
            val view : View = LayoutInflater.from(itemView.context).inflate(R.layout.row_items,layoutItems,false)
            view.findViewById<TextView>(R.id.itemName).text = orderItems.name
            view.findViewById<TextView>(R.id.itemPrice).text = orderItems.subtotal.toPHP()
            view.findViewById<TextView>(R.id.itemQuantity).text = "${orderItems.quantity}x"
            val discount : TextView = view.findViewById(R.id.itemDiscount)
            discount.visibility = View.GONE
            orderItems.options?.let {
                discount.visibility = View.VISIBLE
                discount.text = "${ it.discount }% discount ${orderItems.weight}"
            }
            val productImage : ImageView =  view.findViewById(R.id.itemImage)
            Glide.with(itemView.context).load(orderItems.image).error(R.drawable.product).into(productImage)
            layoutItems.addView(view)
        }
    }



}
package com.cjay.letsmeat.views.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentViewProductBinding
import com.cjay.letsmeat.databinding.FragmentViewTransactionBinding
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.models.transactions.PaymentStatus
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.TransactionType
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.models.transactions.getTotalOrder
import com.cjay.letsmeat.models.transactions.getTotalWeight
import com.cjay.letsmeat.models.transactions.getTotalWithTax
import com.cjay.letsmeat.utils.getShippingDate
import com.cjay.letsmeat.utils.toDateTime
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.views.adapters.TransactionStatusAdapter


class ViewTransaction : Fragment() {

    private lateinit var _binding : FragmentViewTransactionBinding
    private val args by navArgs<ViewTransactionArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewTransactionBinding.inflate(inflater,container,false)
        args.transaction.items.forEach {
            iterateCart(it)
        }
        if (args.transaction.type == TransactionType.PICK_UP) {
            _binding.layoutPickUp.visibility = View.VISIBLE
            _binding.cardShipping.visibility = View.GONE
        } else {
            _binding.layoutPickUp.visibility = View.GONE
            _binding.cardShipping.visibility = View.VISIBLE
        }
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.recyclerviewStatus.apply {
            layoutManager  = LinearLayoutManager(view.context)
            adapter = TransactionStatusAdapter(view.context,args.transaction.details.reversed())
        }

        displayOrderStatus()
        displayOrderDetails()
        displayOrderSummary()
    }

    private fun displayOrderStatus() {
        val details = args.transaction.details.reversed()
        _binding.textCurrentStatusTitle.text = details[0].title
        _binding.textCurrentStatusDesc.text = details[0].description
        val addressInfo = args.transaction.address
        if (addressInfo == null) {
            _binding.layoutAddressInfo.visibility = View.GONE
        }
        _binding.textFullname.text = addressInfo?.contacts?.name ?: "no name"
        _binding.textPhone.text = addressInfo?.contacts?.phone ?: "no phone"
    }

    private fun displayOrderSummary() {
        val items = args.transaction.items
        _binding.textTotalWeight.text = "${items.getTotalWeight() ?: 0} kg"
        _binding.textItemSubtotal.text = items.getTotalWithTax().toPHP()
        _binding.textShippingTotal.text = (args.transaction.shipping?.total ?: 0).toDouble().toPHP()
        _binding.textTotalPayment.text = args.transaction.getTotalOrder().toPHP()
    }

    private fun displayOrderDetails(){
        _binding.textOrdernumber.text = args.transaction.id
        _binding.textOrderDate.text = args.transaction.transactionDate.toDateTime()
        _binding.textPaymentMethod.text = args.transaction.payment?.type?.name ?: "NA"
        _binding.textPaymentDate.text = if (args.transaction.payment?.status == PaymentStatus.PAID) {
            args.transaction.payment?.updatedAt?.toDateTime()
        } else {
            "UNPAID"
        }
        _binding.textShippingDate.text = args.transaction.shipping.getShippingDate()
    }
    private fun iterateCart(orderItems: OrderItems) {
        val view : View = LayoutInflater.from(_binding.root.context).inflate(R.layout.row_items,_binding.layoutItems,false)
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
        Glide.with(_binding.root.context).load(orderItems.image).error(R.drawable.product).into(productImage)
        _binding.layoutItems.addView(view)
    }

    private fun setStatusBgColor(transactionStatus: TransactionStatus): Int {
        return when(transactionStatus) {
            TransactionStatus.PENDING -> ContextCompat.getColor(_binding.root.context, R.color.pending)
            TransactionStatus.ACCEPTED -> ContextCompat.getColor(_binding.root.context, R.color.accepted)
            TransactionStatus.TO_RECEIVE -> ContextCompat.getColor(_binding.root.context, R.color.ondelivery)
            TransactionStatus.TO_PICK_UP -> ContextCompat.getColor(_binding.root.context, R.color.ondelivery)
            TransactionStatus.COMPLETED -> ContextCompat.getColor(_binding.root.context, R.color.completed)
            TransactionStatus.CANCELED -> ContextCompat.getColor(_binding.root.context, R.color.cancelled)
            TransactionStatus.DECLINED -> ContextCompat.getColor(_binding.root.context, R.color.declined)
            TransactionStatus.TO_SHIP -> ContextCompat.getColor(_binding.root.context, R.color.ondelivery)
            TransactionStatus.TO_PACKED ->  ContextCompat.getColor(_binding.root.context, R.color.ondelivery)
        }
    }
}
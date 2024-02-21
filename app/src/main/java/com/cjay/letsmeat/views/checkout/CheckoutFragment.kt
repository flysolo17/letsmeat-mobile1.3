package com.cjay.letsmeat.views.checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentCheckoutBinding
import com.cjay.letsmeat.models.customers.Addresses
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.customers.getDefaultAddress
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.models.transactions.Payment
import com.cjay.letsmeat.models.transactions.PaymentStatus
import com.cjay.letsmeat.models.transactions.PaymentTypes
import com.cjay.letsmeat.models.transactions.Shipping
import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.TransactionType
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.models.transactions.computeTotalTax
import com.cjay.letsmeat.models.transactions.getTotalWeight
import com.cjay.letsmeat.models.transactions.getTotalWithTax
import com.cjay.letsmeat.models.transactions.getTotalWithoutTax
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.calculateShippingFee
import com.cjay.letsmeat.utils.generateRandomNumbers
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.cjay.letsmeat.views.adapters.DeliveryOptionAdapter
import com.cjay.letsmeat.views.adapters.DeliveryOptionClickListener
import com.cjay.letsmeat.views.adapters.PaymentAdapter
import com.cjay.letsmeat.views.adapters.PaymentClickListener
import java.util.Date

val DELIVERY_TYPES = listOf(TransactionType.DELIVERY,TransactionType.PICK_UP)

class CheckoutFragment : Fragment() ,DeliveryOptionClickListener,PaymentClickListener{

    private lateinit var _binding : FragmentCheckoutBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val args by navArgs<CheckoutFragmentArgs>()
    private lateinit var _paymentAdapter : PaymentAdapter
    private lateinit var _deliveryTypeAdapter : DeliveryOptionAdapter
    private var _orderList : List<OrderItems> = emptyList()
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private var _customer : Customers ? = null
    private var _orderTotal = 0.00
    private val _transactionViewModel by activityViewModels<TransactionViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _orderList = args.items.toList()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater,container,false)
        _loadingDialog= LoadingDialog(_binding.root.context)
        args.items.map {
            iterateCart(it)
        }
        _deliveryTypeAdapter = DeliveryOptionAdapter(_binding.root.context, DELIVERY_TYPES,this)
        _binding.recyclerviewDeliveryOptions.apply {
            adapter = _deliveryTypeAdapter
        }
        _paymentAdapter = PaymentAdapter(_binding.root.context, PaymentTypes.values().toList(),this)
        _binding.recyclerviewPayments.apply {
            adapter = _paymentAdapter
        }
        _binding.textItemSubtotal.text = _orderList.getTotalWithTax().toPHP()
        _binding.textTotalWeight.text = "${_orderList.getTotalWeight()} kg"
        displayOrderDetails()
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        _binding.buttonAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_addressesFragment)
        }
        _binding.buttonCheckout.setOnClickListener {
            val shipping = Shipping(weight=  _orderList.getTotalWeight(), total = getShippingFee())
            val paymentType = PaymentTypes.values()[_paymentAdapter.getSelected()]
            val payment = Payment(
             total=_orderTotal,
             status = PaymentStatus.UNPAID,
             type = paymentType
            )
            val type = DELIVERY_TYPES[_deliveryTypeAdapter.getSelected()]
            if (_customer?.getDefaultAddress() == null && type == TransactionType.DELIVERY) {
                Toast.makeText(view.context,"Please add address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (type == TransactionType.PICK_UP && paymentType == PaymentTypes.COD || type == TransactionType.DELIVERY && paymentType == PaymentTypes.PAY_IN_COUNTER) {
                Toast.makeText(view.context,"Invalid payment type ",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            _customer?.let {
                val transactions = Transactions(
                    customerID = it.id?: "",
                    type = type,
                    address =if (type == TransactionType.DELIVERY) {
                        it.getDefaultAddress()
                    } else null,
                    items = _orderList,
                    shipping = if (type == TransactionType.DELIVERY) {shipping} else {null},
                    payment =  payment,
                    details = listOf(TransactionDetails(title = "Order Placed", description = "order successfully placed"))
                )
                saveTransaction(transactions)
            }

        }
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

    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Getting user info..")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    _customer = it.data?.also {    displayAddressInfo(it)  }
                }
            }
        }
    }

    private fun displayAddressInfo(clients: Customers) {
        if (clients.addresses.isNotEmpty()) {
            _binding.layoutAddressInfo.visibility = View.VISIBLE
            _binding.textContact.text = "${clients.addresses[clients.defaultAddress].contacts?.name} | ${clients.addresses[clients.defaultAddress].contacts?.phone}"
            val add = clients.addresses[clients.defaultAddress].addressLine?.split("\n")
            _binding.textAddressLine.text = add?.joinToString(", ")
            _binding.textStreet.text = clients.addresses[clients.defaultAddress].street
        } else {
            _binding.layoutAddressInfo.visibility = View.GONE
        }
    }
    private fun displayOrderDetails() {
        val delivery = DELIVERY_TYPES[_deliveryTypeAdapter.getSelected()]
        var shipping = 0.00
        val total = _orderList.getTotalWithTax()

        if (delivery == TransactionType.DELIVERY) {
            shipping= getShippingFee()

        }
        _binding.textTax.text = _orderList.computeTotalTax().toPHP()
        _binding.textTotalWithoutTax.text = _orderList.getTotalWithoutTax(total).toPHP()
        _binding.textTotalPayment.text = (total + shipping).toPHP()
        _binding.texShippingFee.text = shipping.toPHP()
        _orderTotal = total + shipping
    }

    private fun getShippingFee() : Double {
        return _orderList.getTotalWeight().calculateShippingFee()
    }

    private fun saveTransaction(transactions: Transactions) {
        _transactionViewModel.transactionRepository.createTransaction(transactions) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Saving transaction..")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    if (transactions.payment?.type == PaymentTypes.GCASH) {
                        val directions = CheckoutFragmentDirections.actionCheckoutFragmentToPaymentFragment(transactions)
                        findNavController().navigate(directions)
                        return@createTransaction
                    }
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDeliveryClicked(position: Int) {
        _deliveryTypeAdapter.setDeliveryOption(position)
        displayOrderDetails()
    }

    override fun onPaymentClick(position: Int) {
        _paymentAdapter.selectPayment(position)
        displayOrderDetails()
    }
}
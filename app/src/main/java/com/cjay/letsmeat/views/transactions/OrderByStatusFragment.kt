package com.cjay.letsmeat.views.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentOrderByStatusBinding
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.RatingViewModel
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.cjay.letsmeat.views.adapters.TransactionAdapter
import com.cjay.letsmeat.views.adapters.TransactionClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

const val POSITION = "position"
class OrderByStatusFragment : Fragment() ,TransactionClickListener{
    private var _position: Int ? = null
    private lateinit var _binding : FragmentOrderByStatusBinding
    private lateinit var _loadingDialog: LoadingDialog
    private val _transactionViewModel by activityViewModels<TransactionViewModel>()
    private val _reviewViewModel by activityViewModels<RatingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _position  = it.getInt(POSITION)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderByStatusBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _transactionViewModel.transactions.observe(viewLifecycleOwner) { it ->
            val transactions = it.filter { it.status == TransactionStatus.entries[_position ?: 0] }
            _binding.layoutNoOrder.visibility = if (transactions.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            _binding.recyclerviewTransactions.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = TransactionAdapter(view.context,transactions,this@OrderByStatusFragment,_reviewViewModel)
            }
        }
    }

    override fun cancelTransaction(transactions: Transactions) {
        MaterialAlertDialogBuilder(_binding.root.context)
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel your order. ${transactions.id}")
            .setPositiveButton("Yes") {dialog,_->
                cancelTransaction(transactions.id).also { dialog.dismiss() }
            }
            .setNegativeButton("No" ) {dialog,_->
                dialog.dismiss()
            }
            .show()
    }

    override fun viewTransaction(transactions: Transactions) {
        val directions = TransactionsFragmentDirections.actionMenuTransactionsToViewTransaction(transactions)
        findNavController().navigate(directions)
    }

    override fun payWithGCash(transactions: Transactions) {
        val directions = TransactionsFragmentDirections.actionMenuTransactionsToPaymentFragment(transactions)
        findNavController().navigate(directions)
    }

    override fun rateTransaction(transactions: Transactions,reviews: List<Reviews>) {
        val directions = TransactionsFragmentDirections.actionMenuTransactionsToReviewTransactionFragment(transactions ,reviews.toTypedArray())
        findNavController().navigate(directions)
    }

    private fun cancelTransaction(transactioID: String) {
        _transactionViewModel.transactionRepository.cancelTransaction(transactioID) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }

                is UiState.LOADING -> _loadingDialog.showDialog("Cancelling..")
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
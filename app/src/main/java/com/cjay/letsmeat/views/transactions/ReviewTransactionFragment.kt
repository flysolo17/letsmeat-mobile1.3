package com.cjay.letsmeat.views.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentReviewTransactionBinding
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.cjay.letsmeat.views.adapters.RatingAdapter
import com.cjay.letsmeat.views.adapters.RatingClickListener


class ReviewTransactionFragment : Fragment() ,RatingClickListener{

    private lateinit var _binding : FragmentReviewTransactionBinding
    private  val _transactionViewModel by activityViewModels<TransactionViewModel>()
    private val _args by navArgs<ReviewTransactionFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewTransactionBinding.inflate(inflater,container,false)
        _binding.recyclerViewRating.apply {
            layoutManager = LinearLayoutManager(_binding.root.context)
            adapter = RatingAdapter(_binding.root.context,_args.transaction.items,_args.reviews.toList(),this@ReviewTransactionFragment)
        }
        return _binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRateProduct(items: OrderItems) {
       val directions = ReviewTransactionFragmentDirections.actionReviewTransactionFragmentToRateProductFragment(items,_args.transaction.id)
        findNavController().navigate(directions)
    }


}
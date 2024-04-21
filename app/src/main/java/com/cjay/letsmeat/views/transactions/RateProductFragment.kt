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
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentRateProductBinding
import com.cjay.letsmeat.models.Reviews
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.RatingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RateProductFragment : BottomSheetDialogFragment() {

    private lateinit var _binding : FragmentRateProductBinding
    private val _args by navArgs<RateProductFragmentArgs>()
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private var _customer : Customers ? = null
    private val _reviewViewModel by activityViewModels<RatingViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRateProductBinding.inflate(inflater,container,false)
        _binding.textProductName.text = _args.item.name
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         _authViewModel.customers.observe(viewLifecycleOwner) {
             if (it is UiState.SUCCESS) {
                 _customer = it.data
             }
         }
        _binding.btnRate.setOnClickListener {
            val rating = _binding.ratingBar.rating
            val comment : String = _binding.textComment.text.toString()
            val review = Reviews(transactionID =_args.transactionID,
             userID = _customer?.id ?: "",
             itemID = _args.item.productID,
             rating = rating,
             comment = comment,
            )
            saveReview(review)
        }



    }
    private fun saveReview(reviews: Reviews) {
        _reviewViewModel.createReview(reviews) {
            when(it) {
                is UiState.FAILED -> {
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    println("Loading")
                }
                is UiState.SUCCESS -> {
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                }
            }
        }
    }



}
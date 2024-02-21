package com.cjay.letsmeat.views.nav.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentViewProductBinding
import com.cjay.letsmeat.models.customers.CustomerType
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.product.ProductOptions
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.views.adapters.OptionAdapter


class ViewProductFragment : Fragment() {
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var _binding : FragmentViewProductBinding
    private val _args by navArgs<ViewProductFragmentArgs>()
    private var _customers : Customers ? = null
    private lateinit var _loadingDialog : LoadingDialog

    private lateinit var optionAdapter: OptionAdapter
    private var _selectedOption : ProductOptions ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewProductBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        bindViews(_args.products)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        optionAdapter = OptionAdapter(view.context,_args.products.options)
        _binding.recyclerviewOption.apply {
            layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL,false)
            adapter =optionAdapter
        }
        _binding.buttonBuyNow.setOnClickListener {
            if (_customers == null) {
                findNavController().navigate(R.id.action_viewProductFragment_to_loginFragment)
                return@setOnClickListener
            }
            val options : ProductOptions ? =if  (optionAdapter.getSelectedSize() == -1) {
                null
            } else {
                _args.products.options[optionAdapter.getSelectedSize()]
            }
            val directions = ViewProductFragmentDirections.actionViewProductFragmentToSelectedProductFragment(_args.products,options,true)
            findNavController().navigate(directions)
        }
        _binding.buttonAddToCart.setOnClickListener {
            if (_customers == null) {
                findNavController().navigate(R.id.action_viewProductFragment_to_loginFragment)
                return@setOnClickListener
            }
            val options : ProductOptions ? =if  (optionAdapter.getSelectedSize() == -1) {
                null
            } else {
                _args.products.options[optionAdapter.getSelectedSize()]
            }
            val directions = ViewProductFragmentDirections.actionViewProductFragmentToSelectedProductFragment(_args.products,options,false)
            findNavController().navigate(directions)
        }
    }

    private fun bindViews(products: Products) {
        Glide.with(_binding.root.context).load(products.image).error(R.drawable.product).into(_binding.imageProduct)

//        if(products.comments.isNotEmpty()) {
//            binding.ratingBar.rating = getCommentMedian(products.comments)
//        }
//        binding.textRatingTotal.text = getRatingSum(products.comments).toString()
        _binding.textItemWeight.text ="${products.weight}"
        _binding.textProductName.text = products.name
        _binding.textProductPrice.text = products.price.toPHP()
        _binding.textProductDesc.text = if (!products.description.isNullOrEmpty()) {
            products.description
        } else {
            "No Product Description"
        }
        _binding.textProductDetails.text = if (!products.details.isNullOrEmpty()) {
            products.description
        } else {
            "No Product Description"
        }

        if (products.comments.isEmpty()) {
            _binding.textNoComment.visibility = View.VISIBLE
        } else {
            _binding.textNoComment.visibility = View.GONE
//            binding.recylerviewComments.apply {
//                layoutManager = LinearLayoutManager(binding.root.context)
//                adapter = CommentAdapter(binding.root.context,products.comments)
//            }
            _binding.textCommentCount.text = products.comments.size.toString()
        }

    }

    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
                when(it) {
                    is UiState.FAILED -> {
                        _loadingDialog.closeDialog()
                    }
                    is UiState.LOADING -> {
                        _loadingDialog.showDialog("Getting user information..")
                    }
                    is UiState.SUCCESS -> {
                        _loadingDialog.closeDialog()
                        _customers = it.data
                        _binding.cardOption.visibility = if (it.data?.type === CustomerType.WHOLESALER ) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
        }
    }

}
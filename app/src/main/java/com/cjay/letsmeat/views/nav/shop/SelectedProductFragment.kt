package com.cjay.letsmeat.views.nav.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentSelectedProductBinding
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.product.ProductOptions
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.models.product.getName
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.computeItemSubtotal
import com.cjay.letsmeat.utils.computeItemTotalCost
import com.cjay.letsmeat.utils.convertToKilogram
import com.cjay.letsmeat.utils.generateRandomNumbers
import com.cjay.letsmeat.utils.getDigits
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectedProductFragment : BottomSheetDialogFragment() {


    private lateinit var _binding : FragmentSelectedProductBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private val _cartViewModel by activityViewModels<CartViewModel>()
    private val _args by navArgs<SelectedProductFragmentArgs>()
    private lateinit var _product : Products
    private  var _option : ProductOptions ? = null
    private var _customer : Customers ? = null
    private var _currentQuantity = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _product = _args.product
        _option = _args.option
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedProductBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        if (_args.isCheckout) {
            _binding.buttonAddTocart.visibility = View.GONE
            _binding.buttonBuyNow.visibility = View.VISIBLE
        } else {
            _binding.buttonBuyNow.visibility = View.GONE
            _binding.buttonAddTocart.visibility = View.VISIBLE
        }
        _binding.textProductVariation.visibility = getVisiblity(_option)
        _binding.textDiscount.visibility = getVisiblity(_option)
        _option?.let {
            _binding.textProductVariation.text = it.name
            _binding.textDiscount.text = "${it.discount}% discount"
        }

        _binding.textProductName.text = _product.name
        updatePriceAndQuantity(_currentQuantity)
        Glide.with(_binding.root.context).load(_product.image).error(R.drawable.product).into(_binding.imageSelectedVariation)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        _binding.buttonAdd.setOnClickListener {
            val optionQuantity = _option?.quantity ?: 1
            val nextQuantity = (_currentQuantity + 1) * optionQuantity

            if (_product.stocks > nextQuantity) {
                _currentQuantity += 1
                updatePriceAndQuantity(_currentQuantity)
                return@setOnClickListener
            }
            Toast.makeText(view.context,"You reach the max quantity", Toast.LENGTH_SHORT).show()
        }
        _binding.buttonMinus.setOnClickListener {
            if (_currentQuantity > 1) {
                _currentQuantity -= 1
                updatePriceAndQuantity(_currentQuantity)
                return@setOnClickListener
            }
            Toast.makeText(view.context,"You reach the minimum quantity", Toast.LENGTH_SHORT).show()
        }
        _binding.buttonAddTocart.setOnClickListener {
            _customer?.let {
                val cart  = Cart(userID = it.id , productID = _product.id, option = _option, quantity = _currentQuantity)
                cart.addToCart()
            }
        }
        _binding.buttonBuyNow.setOnClickListener {
            val optionQ = _option?.quantity ?: 1
            val quantityInStocks = _currentQuantity * optionQ
            val wInKG = _product.weight?.convertToKilogram() ?: 0.0
            val items = OrderItems(
                productID = _product.id ?: "",
                name= _product.getName(_option),
                image = _product.image ?: "",
                quantity = _currentQuantity,
                price = _product.price,
                options = _option,
                weight = wInKG * quantityInStocks,
                cost = computeItemTotalCost(_product.cost,_currentQuantity,_option),
                subtotal = computeItemSubtotal(_product.price,_currentQuantity,_option)
            )
            val directions = SelectedProductFragmentDirections.actionSelectedProductFragmentToCheckoutFragment(arrayOf(items))
            findNavController().navigate(directions)
        }
    }

    private fun Cart.addToCart() {
        _cartViewModel.cartRepository.addToCart(this) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Adding to your cart..")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()

                }
            }
        }
    }
    private fun updatePriceAndQuantity(quantity : Int) {
        _binding.textQuantity.text = quantity.toString()
        _binding.textPrice.text = computeItemSubtotal(_product.price,quantity,_option).toPHP()
    }

    private fun getVisiblity(options: ProductOptions ? ) : Int {
        val data =  if (options == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
        return data
    }
    fun observer() {
        _authViewModel.customers.observe(this) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Getting All Data")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    _customer = it.data
                }
            }
        }
    }

}
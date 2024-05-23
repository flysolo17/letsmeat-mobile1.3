package com.cjay.letsmeat.views.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentCartBinding
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.models.cart.getQuantity
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.models.product.getName
import com.cjay.letsmeat.models.transactions.OrderItems
import com.cjay.letsmeat.models.transactions.getTotalWithTax
import com.cjay.letsmeat.repository.cart.CART_COLLECTION
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.computeItemSubtotal
import com.cjay.letsmeat.utils.computeItemTotalCost
import com.cjay.letsmeat.utils.convertToKilogram
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.CartViewModel
import com.cjay.letsmeat.viewmodels.ProductViewModel
import com.cjay.letsmeat.views.adapters.CartAdapter
import com.cjay.letsmeat.views.adapters.CartClickListener
import com.google.firestore.v1.StructuredQuery.Order


class CartFragment : Fragment() ,CartClickListener{
    private lateinit var _binding : FragmentCartBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _cartViewModel by activityViewModels<CartViewModel>()
    private val _productViewModel by activityViewModels<ProductViewModel>()
    private lateinit var _cartAdapter : CartAdapter
    private  var _cartList = emptyList<Cart>()
    private val _selectedCart : MutableList<OrderItems> = mutableListOf()
    private var _productList = listOf<Products>()
    private val _itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Not used for swipe deletion
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Handle swipe deletion here
            val position = viewHolder.adapterPosition
            val cart = _cartList[position]
            removerCart(cart,position)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _productViewModel._products.observe(viewLifecycleOwner) {
            if (it is UiState.SUCCESS) {
                _productList = it.data
            }
        }
        _cartAdapter = CartAdapter(_binding.root.context, _cartList,this@CartFragment)
        _binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(_binding.root.context)
            adapter = _cartAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        val itemTouchHelper = ItemTouchHelper(_itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(_binding.recyclerViewCart)
        observers()
        _binding.buttonCheckout.setOnClickListener {
            var stocksOk = true
            _cartList.forEach { cart ->
                val product = _productList.find { it.id == cart.productID }
                if (product != null && product.stocks < cart.quantity) {
                    stocksOk = false
                    return@forEach  // Exit the loop early if stock is insufficient
                }
            }
            if (!stocksOk) {
                Toast.makeText(view.context, "Insufficient Stocks", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (_selectedCart.isEmpty()) {
                Toast.makeText(view.context,"Please select item to checkout",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val directions = CartFragmentDirections.actionMenuCartToCheckoutFragment(_selectedCart.toTypedArray())
            findNavController().navigate(directions)
        }
    }

    private fun observers() {
        _cartViewModel.carts.observe(viewLifecycleOwner) {
            Log.d(CART_COLLECTION,it.toString())
            if (it is UiState.SUCCESS) {
                _cartList = it.data
                _cartAdapter.updateList(_cartList)
                updateTotal(_selectedCart)
            }
        }
    }

    override fun addQuantity(cartID: String) {
        _cartViewModel.cartRepository.addCartQuantity(cartID) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Adding quantity...")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun decreaseQuantity(cartID: String) {
        _cartViewModel.cartRepository.decreaseCartQuantity(cartID) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Deleting quantity...")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun checkBoxIsClick(isChecked: Boolean,cart: Cart, product: Products,position: Int) {
        val quantityInStocks = cart.getQuantity()
        val wInKG = product.weight?.convertToKilogram() ?: 0.0
        val items = OrderItems(
            productID = product.id ?: "",
            name= product.getName(cart.option),
            image = product.image ?: "",
            quantity = cart.quantity,
            price = product.price,
            options = cart.option,
            weight = wInKG * quantityInStocks,
            cost = computeItemTotalCost(product.cost,cart.quantity,cart.option),
            subtotal = computeItemSubtotal(product.price,cart.quantity,cart.option)
        )
        if (isChecked) {
            _selectedCart.add(items)
        } else {
            _selectedCart.removeAt(position)
        }
        updateTotal(_selectedCart)
    }

    private fun removerCart(cart: Cart,position : Int) {
        _cartViewModel.cartRepository.removeToCart(cart.id ?: "") {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Deleting...")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    _cartAdapter.notifyItemRemoved(position)
                    _binding.recyclerViewCart.adapter?.notifyItemRemoved(position)
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotal(items : List<OrderItems>) {
        _binding.textTotal.text = items.getTotalWithTax().toPHP()
    }

    override fun onResume() {
        super.onResume()
        _selectedCart.clear()
        updateTotal(_selectedCart)
    }


}
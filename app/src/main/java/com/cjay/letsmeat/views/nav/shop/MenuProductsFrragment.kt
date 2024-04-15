package com.cjay.letsmeat.views.nav.shop

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentMenuProductsFrragmentBinding
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.ProductViewModel
import com.cjay.letsmeat.views.adapters.ProductAdapterClickListener
import com.cjay.letsmeat.views.adapters.ProductsAdapter

const val PRODUCTS = "products"
class MenuProductsFrragment : Fragment() ,ProductAdapterClickListener{

    private var products: List<Products>? = null
    private lateinit var _binding : FragmentMenuProductsFrragmentBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            products = it.getParcelableArrayList(PRODUCTS)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuProductsFrragmentBinding.inflate(inflater,container,false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productsAdapter = ProductsAdapter(_binding.root.context,products ?: emptyList(),this@MenuProductsFrragment)
        _binding.recyclerviewProducts.apply {
            layoutManager = GridLayoutManager(_binding.root.context,2)
            adapter = productsAdapter
        }
        implementSearch(productsAdapter)
    }

    companion object {
        @JvmStatic
        fun newInstance(products: ArrayList<Products>) =
            MenuProductsFrragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(PRODUCTS, products)
                }
            }
    }


    private fun implementSearch(adapter: ProductsAdapter) {
        val handler = Handler(Looper.getMainLooper())
        var searchHandler: Runnable? = null
        _binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
                searchHandler?.let { handler.removeCallbacks(it) }
            }
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(editable: Editable) {
                val query = editable.toString().trim()
                searchHandler?.let { handler.removeCallbacks(it) }
                searchHandler = Runnable {
                    adapter.filter(query)
                }
                handler.postDelayed(searchHandler!!, 500)
            }
        })
    }
    override fun onProductionClick(products: Products) {
        val directions = ShopFragmentDirections.actionMenuShopToViewProductFragment(products)
        findNavController().navigate(directions)
    }
}
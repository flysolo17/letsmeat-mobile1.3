package com.cjay.letsmeat.views.nav.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cjay.letsmeat.databinding.FragmentShopBinding
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.ProductViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ShopFragment : Fragment() {

    private lateinit var _binding : FragmentShopBinding
    private val _productViewModel  by activityViewModels<ProductViewModel>()
    private lateinit var _loading  : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopBinding.inflate(inflater,container,false)
        _loading= LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _productViewModel.getAllProducts()
        _productViewModel._products.observe(viewLifecycleOwner) { it ->
            when(it) {
                is UiState.FAILED -> {
                    _loading.closeDialog()
                    Toast.makeText(view.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> _loading.showDialog("Getting All Products")
                is UiState.SUCCESS -> {
                    _loading.closeDialog()
                    val categories = mutableListOf<String>()
                    categories.add("ALL")
                    categories.addAll(it.data.map { it.brand ?: "" }.distinct())
                    attachTabs(categories,it.data)

                }
            }
        }
    }
    private fun attachTabs(categories: List<String>,products: List<Products>) {
        val indicator = ProductTabAdapter(this,categories,products)
        TabLayoutMediator(_binding.tabLayout,_binding.pager2.apply { adapter = indicator },true) {tab,position ->
            tab.text =categories[position].uppercase()
        }.attach()

    }


    class ProductTabAdapter(val fragment: Fragment, private val categories : List<String>,private val products: List<Products>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return categories.size
        }
        override fun createFragment(position: Int): Fragment {
            val selected: ArrayList<Products> = if (position == 0) {
                ArrayList(products)
            } else {
                ArrayList(products.filter { it.brand == categories[position] })
            }

            return MenuProductsFrragment.newInstance(products = selected)
        }
    }

}
package com.cjay.letsmeat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.repository.products.ProductRepository
import com.cjay.letsmeat.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    private val _productList= MutableLiveData<UiState<List<Products>>>()
    val _products : LiveData<UiState<List<Products>>> get() = _productList

    fun getAllProducts() {
        productRepository.getAllProducts {
            _productList.value = it
        }
    }
    fun setProduct(products: UiState<List<Products>>) {
        _productList.value = products
    }
}
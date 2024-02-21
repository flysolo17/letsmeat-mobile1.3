package com.cjay.letsmeat.repository.products

import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.utils.UiState



interface ProductRepository {
    fun getAllProducts(result : (UiState<List<Products>>) -> Unit)

}
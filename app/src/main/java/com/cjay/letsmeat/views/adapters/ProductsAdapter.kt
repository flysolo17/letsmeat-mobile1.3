package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.models.transactions.computeProductSold
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.TransactionViewModel


interface ProductAdapterClickListener {
    fun onProductionClick(products: Products)
}
class ProductsAdapter(private val context : Context, private var productList : List<Products>,
                      var transactionList : List<Transactions>,
                      private val productAdapterClickListener: ProductAdapterClickListener
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    private val originalList: List<Products> = productList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_products,parent,false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val products = productList[position]
        holder.textProductName.text = products.name
        holder.textPrice.text = products.price.toPHP()
        Glide.with(context).load(products.image).error(R.drawable.product).into(holder.imageProduct)
        holder.itemView.setOnClickListener {
            productAdapterClickListener.onProductionClick(products)
        }
        holder.displayItemSold(products.id ?: "",transactionList)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    fun filter(query: String) {
        productList = if (query.isBlank()) {

            originalList
        } else {

            originalList.filter { it.name!!.contains(query, ignoreCase = true) }
            }
        notifyDataSetChanged()
    }
    fun setTransactions(transactionList: List<Transactions>) {
        this.transactionList = transactionList;
        notifyDataSetChanged()
    }
    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textProductName : TextView = itemView.findViewById(R.id.textProductName)
        val textPrice : TextView = itemView.findViewById(R.id.textPrice)
        val imageProduct : ImageView = itemView.findViewById(R.id.imageProduct)
        val textProductSold : TextView = itemView.findViewById(R.id.itemSold)

        fun displayItemSold(productID : String ,transactionList: List<Transactions>) {
            val count = transactionList.filter { it.status == TransactionStatus.COMPLETED }
                .sumOf { it.items.computeProductSold(productID) }
            textProductSold.text = count.toString()
        }
    }
}
package com.cjay.letsmeat.views.adapters

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.cart.Cart
import com.cjay.letsmeat.models.cart.getNextQuantity
import com.cjay.letsmeat.models.cart.getQuantity
import com.cjay.letsmeat.models.product.Products
import com.cjay.letsmeat.repository.products.PRODUCT_COLLECTION
import com.cjay.letsmeat.utils.computeItemSubtotal
import com.cjay.letsmeat.utils.toPHP
import com.google.firebase.firestore.FirebaseFirestore



interface CartClickListener {
    fun addQuantity(cartID: String)
    fun decreaseQuantity(cartID: String)
    fun checkBoxIsClick(isChecked :Boolean,cart: Cart,product: Products,position: Int)
}
class CartAdapter(private val context: Context, private var carts : List<Cart>, private val cartClickListener: CartClickListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val  view = LayoutInflater.from(context).inflate(R.layout.adapter_cart,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = carts[position]
        val firestore = FirebaseFirestore.getInstance()
        holder.getProduct(firestore,cart)

        holder.textQuantity.text = cart.quantity.toString()
        cart.option?.let {
            holder.textVariation.text = "${it.name} (${it.discount} % discount)"
        }
        holder.textVariation.visibility = if (cart.option == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
        holder.checkOut.setOnClickListener {
            if (holder.product != null) {
                cartClickListener.checkBoxIsClick(holder.checkOut.isChecked,cart,holder.product!!,position)
            }
        }
        holder.buttonAdd.setOnClickListener {
            val nextQ =  cart.getNextQuantity()
            if ((holder.product?.stocks ?: 0) > nextQ) {
                cartClickListener.addQuantity(cartID = cart.id ?: "")
                return@setOnClickListener
            }
            Toast.makeText(context,"You reach the maximum quantity",Toast.LENGTH_SHORT).show()
        }
        holder.buttonMinus.setOnClickListener {
            if (cart.quantity > 1) {
                cartClickListener.decreaseQuantity(cartID = cart.id ?: "")
                return@setOnClickListener
            }
            Toast.makeText(context,"You reach the minimum quantity",Toast.LENGTH_SHORT).show()
        }
    }
    fun updateList(list : List<Cart>) {
        this.carts = list
        notifyDataSetChanged()
    }
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val textName : TextView = itemView.findViewById(R.id.textProductName)
        val textVariation : TextView = itemView.findViewById(R.id.textProductOption)
        val buttonAdd : ImageButton = itemView.findViewById(R.id.buttonAdd)
        val buttonMinus : ImageButton = itemView.findViewById(R.id.buttonMinus)
        val textQuantity : TextView = itemView.findViewById(R.id.textQuantity)
        private val textPrice : TextView = itemView.findViewById(R.id.textPrice)
        val checkOut : CheckBox = itemView.findViewById(R.id.checkOut)
        private val imageProduct : ImageView = itemView.findViewById(R.id.imageProduct)
        var product : Products ? = null
        fun getProduct(firestore: FirebaseFirestore, cart: Cart) {
            firestore.collection(PRODUCT_COLLECTION)
                .document(cart.productID ?: "")
                .get()
                .addOnSuccessListener { productDoc ->
                    if (productDoc.exists()) {
                        val product = productDoc.toObject(Products::class.java)
                        this.product = product
                        Glide.with(itemView.context).load(product?.image).error(R.drawable.product).into(imageProduct)
                        textName.text = product?.name ?: "No Product"
                        textPrice.text = computeItemSubtotal(product?.price ?: 0.00,cart.quantity,cart.option).toPHP()
                    }
                }
        }

    }
}
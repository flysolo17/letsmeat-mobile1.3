package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.product.ProductOptions
import com.google.android.material.card.MaterialCardView

class OptionAdapter(private val  context: Context,private val options : List<ProductOptions>) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var selectedOption = -1;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_options,parent,false)
        return OptionViewHolder(view)
    }

    fun getSelectedSize()  : Int {
        return selectedOption
    }
    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.textSize.text =option.name
        holder.textDiscount.text = "${option.discount}% discount"
        holder.itemView.setOnClickListener {
            selectedOption = if (selectedOption == holder.adapterPosition) {
                -1
            } else {
                holder.adapterPosition
            }
            notifyDataSetChanged()
        }

        if (position == selectedOption) {
            holder.cardSize.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.textSize.setTextColor(ContextCompat.getColor(context, R.color.green200))
        } else {
            holder.cardSize.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green200))
            holder.textSize.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
    class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textSize : TextView = itemView.findViewById(R.id.textOption)
        val textDiscount : TextView = itemView.findViewById(R.id.textDiscount)
        val cardSize : MaterialCardView = itemView.findViewById(R.id.cardOption)
    }
}
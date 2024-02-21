package com.cjay.letsmeat.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cjay.letsmeat.R
import com.cjay.letsmeat.models.customers.Addresses
interface AddressClickListener {
    fun onAddressClicked(position: Int)
}
class AddressesAdapter(private val context: Context,private val addressList : List<Addresses>, private var default : Int,private val addressClickListener: AddressClickListener) : RecyclerView.Adapter<AddressesAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_addresses,parent,false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val  address = addressList[position]
        holder.textFullname.text = address.contacts?.name
        holder.textPhone.text = address.contacts?.phone
        holder.textStreet.text = address.street
        val add = address.addressLine?.split("\n")
        holder.textAddressLine.text = add?.joinToString(", ")
        holder.textPostalCode.text = address.postalCode.toString()
        if (position == default) {
            holder.textDefaultAddress.visibility = View.VISIBLE
        } else {
            holder.textDefaultAddress.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            addressClickListener.onAddressClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }
    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFullname : TextView = itemView.findViewById(R.id.textFullname)
        val textPhone : TextView = itemView.findViewById(R.id.textPhoneNumber)
        val textStreet : TextView = itemView.findViewById(R.id.textStreet)
        val textAddressLine : TextView = itemView.findViewById(R.id.textAddressLine)
        val textPostalCode : TextView = itemView.findViewById(R.id.textPostalCode)
        val textDefaultAddress : TextView = itemView.findViewById(R.id.textDefault)
    }

    fun changeDefault(position: Int) {
        default = position
        notifyDataSetChanged()
    }
}
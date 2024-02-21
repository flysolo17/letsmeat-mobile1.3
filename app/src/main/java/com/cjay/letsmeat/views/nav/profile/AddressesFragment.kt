package com.cjay.letsmeat.views.nav.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentAddressesBinding
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.views.adapters.AddressClickListener
import com.cjay.letsmeat.views.adapters.AddressesAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.checkerframework.checker.guieffect.qual.UI

class AddressesFragment : Fragment() ,AddressClickListener{
    private lateinit var _binding : FragmentAddressesBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var _addressAdapter  : AddressesAdapter
    private var _customer : Customers ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressesBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        _binding.buttonCreateAddress.setOnClickListener {
            _customer?.let {
                val directions = AddressesFragmentDirections.actionAddressesFragmentToCreateAddressFragmemt(it)
                findNavController().navigate(directions)
            }
        }

    }

    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
            if (it is UiState.SUCCESS) {
                _customer = it.data
                _customer?.let {c->
                    _addressAdapter = AddressesAdapter(_binding.root.context,c.addresses,c.defaultAddress,this@AddressesFragment)
                    _binding.recyclerviewAddresses.apply {
                        layoutManager = LinearLayoutManager(_binding.root.context)
                        adapter = _addressAdapter
                    }
                }

            }
        }
    }

    override fun onAddressClicked(position: Int) {
        displayChangeAddressDialog(position)
    }
    private fun changeDefaultAddress(position: Int) {
        _authViewModel.authRepository.changeDefaultAddress(_customer?.id?:"",position) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Changing Default Address...")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    _addressAdapter.changeDefault(position)
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun displayChangeAddressDialog(position: Int) {
        MaterialAlertDialogBuilder(_binding.root.context)
            .setTitle("Change Default Address")
            .setMessage("Are you sure you want to change your default address?")
            .setNegativeButton("Cancel") { dialog ,_ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") {dialog,_ ->
                changeDefaultAddress(position)
                dialog.dismiss()
            }
            .show()
    }

}
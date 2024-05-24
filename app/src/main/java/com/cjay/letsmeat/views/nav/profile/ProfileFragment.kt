package com.cjay.letsmeat.views.nav.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentProfileBinding
import com.cjay.letsmeat.models.customers.CustomerType
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.CartViewModel
import com.cjay.letsmeat.views.adapters.CartAdapter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


 class ProfileFragment : Fragment() {
    private lateinit var _binding : FragmentProfileBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private var _customer : Customers ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        _binding.buttonLogout.setOnClickListener {
            _authViewModel.authRepository.logout().also {
                findNavController().navigate(R.id.menu_shop)
            }
        }
        _binding.buttonTerms.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_termsFragment)
        }
        _binding.buttonPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_privacyFragment)
        }
        _binding.buttonEditProfile.setOnClickListener {
            _customer?.let {
                val directions = ProfileFragmentDirections.actionMenuProfileToEditProfileFragment(it)
                findNavController().navigate(directions)
            }
        }

        _binding.buttonAddresses.setOnClickListener {
            _customer?.let {
                findNavController().navigate(R.id.action_menu_profile_to_addressesFragment)
            }
        }

        displayViews(_customer)
        _binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_loginFragment)
        }
        _binding.buttonDeleteAccount.setOnClickListener {
            val  user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                MaterialAlertDialogBuilder(view.context)
                    .setTitle("Delete Account")
                    .setMessage(
                        "Are you sure you want to delete your account ? if yes all your data will be deleted such as Messages , Cart ," +
                                " User Info and uncompleted orders will be automatically removed"
                    ).setPositiveButton("Confirm") { dialog , _ ->
                        deleteAccount(_customer?.id ?: "",user).also {
                            dialog.dismiss()
                        }

                    }.setNegativeButton("Cancel") { a,dialog ->
                        a.dismiss()
                    }.show()
            } else {
                Toast.makeText(view.context,"User not found",Toast.LENGTH_SHORT).show()
            }

        }
    }

     private fun deleteAccount(uid : String,firebaseUser: FirebaseUser) {
         _authViewModel.deleteAccount(uid,firebaseUser) {
             when(it) {
                 is UiState.FAILED -> {
                     _loadingDialog.closeDialog()
                     Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                 }
                 is  UiState.LOADING -> {
                     _loadingDialog.showDialog("Deleting account")
                 }
                 is UiState.SUCCESS -> {

                     _loadingDialog.closeDialog()
                     Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                     displayViews(null)

                 }
             }
         }
     }
    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
           if (it is UiState.SUCCESS) {
                _loadingDialog.closeDialog()
                _customer = it.data
              _binding.textOrderType.visibility = if (_customer?.type == CustomerType.WHOLESALER) {
                  View.VISIBLE
              } else {
                  View.GONE
              }
               displayViews(_customer)
            }
        }
    }
    private fun displayViews(customers: Customers ?) {
        if (customers == null) {
            _binding.layoutNoUser.visibility = View.VISIBLE
            _binding.layoutProfile.visibility = View.GONE
        } else {
            _binding.layoutNoUser.visibility = View.GONE
            _binding.layoutProfile.visibility = View.VISIBLE
            bindViews(customers)
        }
    }
    private fun bindViews(customers: Customers) {
        _binding.textFullname.text = customers.fullname
        _binding.textEmail.text = customers.phone ?: "unknown user"
        Glide.with(_binding.root.context)
            .load(customers.profile)
            .error(R.drawable.profile)
            .into(_binding.imageProfile)
    }

}
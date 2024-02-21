package com.cjay.letsmeat.views.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R

import com.cjay.letsmeat.databinding.FragmentEditProfileBinding
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.IOException


class EditProfileFragment : Fragment() {

    private lateinit var _binding : FragmentEditProfileBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _args by navArgs<EditProfileFragmentArgs>()



    private var _imageURI: Uri? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                if (data?.data != null) {
                    _imageURI = data.data
                    _imageURI?.let {
                        Glide.with(_binding.root.context).load(it).error(R.drawable.profile).into(_binding.imageProfile)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    private val _authViewModel : AuthViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        displayViews(_args.customer)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.buttonAddProfile.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        _binding.buttonSave.setOnClickListener {
            if (_imageURI != null) {
                uploadProfile("" ,_imageURI!!)
                return@setOnClickListener
            } else {
                _args.customer.fullname = _binding.inputFullName.text.toString()
               updateCustomerInfo(_args.customer)
            }
        }
    }

    private fun uploadProfile(uid: String, _imageURI: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            _authViewModel.authRepository.uploadProfile(_imageURI,uid) {
                when(it) {
                    is UiState.FAILED -> {
                        _loadingDialog.closeDialog()
                        Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                    }
                    is UiState.LOADING -> {
                        _loadingDialog.showDialog("Uploading image....")
                    }
                    is UiState.SUCCESS -> {
                        _loadingDialog.closeDialog()
                        _args.customer.fullname = _binding.inputFullName.text.toString()
                        _args.customer.profile = it.data.toString()
                        updateCustomerInfo(_args.customer)
                    }
                }
            }
        }
    }
    private fun updateCustomerInfo(customers: Customers) {
        _authViewModel.authRepository.updateAccount(customers) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Uploading image....")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun displayViews(clients: Customers) {
        clients.profile.let {
            Glide.with(_binding.root.context).load(it).error(R.drawable.profile).into(_binding.imageProfile)
        }
        _binding.inputFullName.setText(clients.fullname)
    }
}
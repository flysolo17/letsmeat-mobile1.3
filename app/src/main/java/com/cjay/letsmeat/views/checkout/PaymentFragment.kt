package com.cjay.letsmeat.views.checkout

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentPaymentBinding
import com.cjay.letsmeat.models.transactions.Payment
import com.cjay.letsmeat.models.transactions.PaymentStatus
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.toPHP
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date


class PaymentFragment : Fragment() {
    private lateinit var _binding : FragmentPaymentBinding
    private lateinit var _loadingDialog : LoadingDialog

    private var _imageURI: Uri? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                if (data?.data != null) {
                    _imageURI = data.data
                    _imageURI?.let {
                        Glide.with(_binding.root.context).load(it).error(R.drawable.product).into(_binding.imageReceipt)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    private val args by navArgs<PaymentFragmentArgs>()
    private val _transactioNViewModel by activityViewModels<TransactionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        _binding.textPayment.text = args.transaction.payment?.total?.toPHP()
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.buttonAttach.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
        _binding.buttonConfirmPayment.setOnClickListener {
            if (_imageURI == null) {
                Toast.makeText(view.context,"Please Add Receipt",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            _imageURI?.let {
                uploadReceipt(_imageURI!!,args.transaction.payment!!)
            }

        }
    }
    private fun uploadReceipt(uri: Uri,payment: Payment) {
        viewLifecycleOwner.lifecycleScope.launch {
            _transactioNViewModel.transactionRepository.uploadReceipt(uri){
                when(it){
                    is UiState.FAILED -> {
                        _loadingDialog.closeDialog()
                        Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                    }
                    is UiState.LOADING -> {
                        _loadingDialog.showDialog("Uploading receipt..")
                    }
                    is UiState.SUCCESS -> {
                        _loadingDialog.closeDialog()
                        payment.receipt = it.data
                        payment.updatedAt = Date()
                        payment.status = PaymentStatus.PAID
                        confirmPayment(args.transaction.id,payment)
                    }
                }
            }
        }
    }
    private fun confirmPayment(transactionID : String,payment: Payment) {
        _transactioNViewModel.transactionRepository.addPayment(transactionID,payment) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Saving payment..")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    findNavController().popBackStack()

                }
            }
        }
    }
}
package com.cjay.letsmeat.views.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.createViewModelLazy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cjay.letsmeat.databinding.FragmentOtpViewBinding
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.concurrent.TimeUnit


class OtpViewFragment : DialogFragment() {
    private lateinit var _binding : FragmentOtpViewBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _args by navArgs<OtpViewFragmentArgs>()
    private val _authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpViewBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        verificationCodeCountDown()
        _binding.textPhoneNumber.text = "+63 ${_args.phone.phone}"
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        var options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+63${_args.phone.phone}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: ForceResendingToken
                ) {

                    verificationCodeCountDown()
                    _binding.textPhoneNumber.text = "+63 ${_args.phone.phone}"
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signInWithPhoneCredential(phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(_binding.root.context,e.message,Toast.LENGTH_SHORT).show()
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options);
        _binding.buttonConfirm.setOnClickListener {
            val input = _binding.inputPinView.text.toString()
            if (input.isNotEmpty() && input.length == 6) {
                verifyOtp(_args.phone.code?: "",input)
                return@setOnClickListener
            }
            Toast.makeText(view.context,"Invalid code", Toast.LENGTH_SHORT).show()
        }
    }


    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        _authViewModel.authRepository.signInWithPhoneAuthCredential(credential) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Signing in...")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()

                }
            }
        }
    }

    private fun verifyOtp(verificationCode: String,OTP: String) {
        _authViewModel.authRepository.verifyOTP(verificationCode,OTP) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("verifying otp....")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    checkUser(it.data)
                }
            }
        }
    }
    private fun checkUser(firebaseUser: FirebaseUser) {
        _authViewModel.authRepository.checkUserByID(firebaseUser) {
            when (it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context, it.message, Toast.LENGTH_SHORT).show()
                }

                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Checking user info....")
                }

                is UiState.SUCCESS -> {
                    _authViewModel._customers.value = it
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,"Successfully Logged in",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    private fun verificationCodeCountDown() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _binding.buttonResendOTP.text = "" + millisUntilFinished / 1000
                _binding.buttonResendOTP.isEnabled = false
            }
            override fun onFinish() {
                _binding.buttonResendOTP.text = "Resend"
                _binding.buttonResendOTP.isEnabled = true
            }
        }.start()
    }

}
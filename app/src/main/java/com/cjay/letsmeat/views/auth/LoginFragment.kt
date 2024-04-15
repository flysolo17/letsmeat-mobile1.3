package com.cjay.letsmeat.views.auth

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentLoginBinding
import com.cjay.letsmeat.models.customers.PhoneAuth
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

const val TAG ="Login"
class LoginFragment : Fragment() {

    private lateinit var _binding : FragmentLoginBinding
    private lateinit var _loadingDialog: LoadingDialog
    private val _authViewModel : AuthViewModel by activityViewModels()
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Toast.makeText(_binding.root.context,e.toString(),Toast.LENGTH_LONG).show()
                }
                is FirebaseTooManyRequestsException -> {
                    Toast.makeText(_binding.root.context,e.toString(),Toast.LENGTH_LONG).show()
                }
                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    Toast.makeText(_binding.root.context,e.toString(),Toast.LENGTH_LONG).show()
                }
                is FirebaseAuthException -> {
                    Toast.makeText(_binding.root.context,e.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            val phone  = _binding.inputPhone.text.toString()
            val phoneAuth = PhoneAuth(phone,token,verificationId)
            Log.d(TAG,verificationId)
            val directions = LoginFragmentDirections.actionLoginFragmentToOtpViewFragment(phoneAuth)
            findNavController().navigate(directions)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        val spannableStringBuilder = SpannableStringBuilder(_binding.textTerms.text)
        val termsAndConditionsSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_termsFragment)
            }
        }
        val privacyPolicySpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event, e.g., open the privacy policy page
                findNavController().navigate(R.id.action_loginFragment_to_privacyFragment)
            }
        }
        spannableStringBuilder.setSpan(termsAndConditionsSpan, 53, 73, 0)
        spannableStringBuilder.setSpan(privacyPolicySpan, 98, 112, 0)

        _binding.textTerms.text = spannableStringBuilder
        _binding.textTerms.movementMethod = LinkMovementMethod.getInstance()

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        _binding.buttonContinue.setOnClickListener {
            val phone  = _binding.inputPhone.text.toString()
            if (phone.startsWith("9") && phone.length == 10) {
                sendOTP(phone)
                return@setOnClickListener
            }
            _binding.inputPhoneLayout.error = "Invalid Phone number"
            Toast.makeText(view.context,"Invalid", Toast.LENGTH_SHORT).show()

        }
    }
    private fun sendOTP(phone : String) {
        val auth = FirebaseAuth.getInstance()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+63${phone}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Getting user information..")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()

                    it.data?.let {
                        findNavController().popBackStack()
                        findNavController().popBackStack()
                    }
                }
            }
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


}
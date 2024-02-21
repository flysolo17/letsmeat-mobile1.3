package com.cjay.letsmeat.views.nav.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentMessagesBinding
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.messages.Messages
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.MessagesViewModel
import com.cjay.letsmeat.views.adapters.MessageAdapter
import com.google.firebase.auth.FirebaseAuth


class MessagesFragment : Fragment() {

    private lateinit var _binding : FragmentMessagesBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _authViewModel by activityViewModels<AuthViewModel>()
    private val _messagesViewModel by activityViewModels<MessagesViewModel>()
    private var _customer : Customers ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessagesBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        _binding.buttonMessages.setOnClickListener {
            val message = _binding.inputMessages.text.toString()
            if (_customer == null) {
                Toast.makeText(view.context,"Please login",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (message.isEmpty()) {
                Toast.makeText(view.context,"enter message",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val messages = Messages(
                senderID = _customer?.id ?: "",
                receiverID = "EmrofYlFBtOG4c2gF5uhMIssxuD3",
                message = message,
            )
            sendMessage(messages)
        }
    }

    private fun observers() {
        _authViewModel.customers.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Getting Profile info")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    _customer = it.data
                  if (it.data == null) {
                      _binding.layoutNoUser.visibility = View.VISIBLE
                      _binding.parentmessages.visibility = View.GONE

                  } else {
                      _binding.parentmessages.visibility = View.VISIBLE
                      _binding.layoutNoUser.visibility = View.GONE
                  }
                }
            }
        }

        _messagesViewModel.messages.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _loadingDialog.showDialog("Getting all messages!!")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    val messagesAdapter = MessageAdapter(_binding.root.context,it.data, _customer!!)
                    val linearLayoutManager = LinearLayoutManager(_binding.root.context)
                    linearLayoutManager.reverseLayout = true
                    linearLayoutManager.stackFromEnd = true
                    _binding.recyclerviewMessages.apply {
                        layoutManager = linearLayoutManager
                        adapter = messagesAdapter
                    }
                }

            }
        }
    }


    private fun sendMessage(messages: Messages) {
        _messagesViewModel.messagesRepository.sendMessage(messages) {
            when(it) {
                is UiState.FAILED -> {
                    _binding.buttonMessages.isClickable = true
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.LOADING -> {
                    _binding.buttonMessages.isClickable = false
                }
                is UiState.SUCCESS -> {
                    _binding.buttonMessages.isClickable = true
                    _binding.inputMessages.setText("")
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}
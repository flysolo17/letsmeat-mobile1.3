package com.cjay.letsmeat.views.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentNotificationBinding
import com.cjay.letsmeat.models.Notifications
import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.models.transactions.isToday
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.cjay.letsmeat.views.adapters.NotificationAdapter
import com.cjay.letsmeat.views.adapters.NotificationClickListener


class NotificationFragment : Fragment() ,NotificationClickListener{

    private lateinit var _binding : FragmentNotificationBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _transactionViewModel by activityViewModels<TransactionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _transactionViewModel.transactions.observe(viewLifecycleOwner) {
            val notif : MutableList<Notifications> = mutableListOf()
            it.map {t->
                t.details.map {d->
                    if (d.createdAt.isToday()) {
                        notif.add(Notifications(t,d))
                    }
                }
            }
            _binding.recyclerviewNotifications.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = NotificationAdapter(view.context,notif,this@NotificationFragment)
            }
        }
    }

    override fun onNotificationClicked(transactions: Transactions) {

    }
}
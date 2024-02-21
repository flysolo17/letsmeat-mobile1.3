package com.cjay.letsmeat.views.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentTransactionsBinding
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.transactions.TransactionStatus
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.google.android.material.tabs.TabLayoutMediator


class TransactionsFragment : Fragment() {

    private lateinit var _binding : FragmentTransactionsBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _transactionsViewModel by activityViewModels<TransactionViewModel>()
    private val _authViewModel by activityViewModels<AuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        attachTabs()
    }
    private fun observers() {
        _transactionsViewModel
    }



    private fun attachTabs() {
        val indicator = PurchasesTabAdapter(this, TransactionStatus.entries)
        TabLayoutMediator(_binding.tabLayout,_binding.pager2.apply { adapter = indicator },true) {tab,position ->
            tab.text =TransactionStatus.entries[position].toString().replace("_"," ")
        }.attach()
//        binding.tabLayout.getTabAt()!!.select()

    }
    class PurchasesTabAdapter(val fragment: Fragment, private val statusList : List<TransactionStatus>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return statusList.size
        }
        override fun createFragment(position: Int): Fragment {
            val fragment = OrderByStatusFragment()
            fragment.arguments = Bundle().apply {
                putInt(POSITION,position)
            }
            return fragment
        }
    }
}
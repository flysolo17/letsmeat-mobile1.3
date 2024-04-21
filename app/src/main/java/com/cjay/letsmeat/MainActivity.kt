package com.cjay.letsmeat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cjay.letsmeat.databinding.ActivityMainBinding
import com.cjay.letsmeat.models.customers.Customers
import com.cjay.letsmeat.models.transactions.TransactionDetails
import com.cjay.letsmeat.models.transactions.Transactions
import com.cjay.letsmeat.models.transactions.isToday
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.utils.getLastMessage
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.viewmodels.CartViewModel
import com.cjay.letsmeat.viewmodels.MessagesViewModel
import com.cjay.letsmeat.viewmodels.ProductViewModel
import com.cjay.letsmeat.viewmodels.TransactionViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@ExperimentalBadgeUtils @AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val loadingDialog = LoadingDialog(this)
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var navController : NavController
    private val productViewModel by viewModels<ProductViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private val transactionViewModel by viewModels<TransactionViewModel>()
    private val messagesViewModel by viewModels<MessagesViewModel>()
    private lateinit var badge: BadgeDrawable
    private lateinit var badgeTransactions: BadgeDrawable
    private lateinit var messagesbagde: BadgeDrawable
    private var customer : Customers ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        badgeTransactions = BadgeDrawable.create(this)
        badge = BadgeDrawable.create(this)
        messagesbagde = BadgeDrawable.create(this)
        setUpNav()

    }
    private fun setUpNav() {
        observer()
        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_container)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menu_shop,
                R.id.menu_transactions,
                R.id.menu_profile,)
        )
        badgeTransactions =   navView.getOrCreateBadge(R.id.menu_transactions)
        BadgeUtils.attachBadgeDrawable(badgeTransactions, binding.bottomAppBar, R.id.menu_transactions)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        productViewModel.getAllProducts()
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.menu_shop -> {
                    showBottomNav()
                    invalidateOptionsMenu()
                }
                R.id.menu_profile -> {
                    showBottomNav()
                    invalidateOptionsMenu()
                }
                R.id.menu_transactions -> {
                    showBottomNav()

                }
                else -> {
                    hideBottomNav()
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showBottomNav() {
        binding.bottomAppBar.performShow(true)
        binding.bottomAppBar.hideOnScroll = true
    }

    private fun hideBottomNav() {
        binding.bottomAppBar.performHide(true)
        binding.bottomAppBar.hideOnScroll = false
    }
    private fun observer() {
        authViewModel.customers.observe(this) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Getting All Data")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    customer = it.data
                    it.data?.let { c ->
                        messagesViewModel.getAllMessages(c.id ?: "")
                        cartViewModel.getAllProductsInCart(it.data.id ?: "")
                        transactionViewModel.transactionRepository
                            .getAllTransactionByCustomerID(c.id ?:"") {
                                if (it is UiState.SUCCESS) {
                                    transactionViewModel.setTransactionList(it.data)
                                    val count = countTransactionsToday(it.data)
                                    badgeTransactions.number = count


                                }
                        }
                    }
                }
            }
        }
        cartViewModel.carts.observe(this) {
            when(it) {
                is UiState.FAILED -> {
                }
                is UiState.LOADING -> {

                }
                is UiState.SUCCESS -> {
                    badge.number = it.data.size
                }
            }
        }
        messagesViewModel.messages.observe(this) {
            if (it is UiState.SUCCESS) {
                messagesbagde.number = it.data.getLastMessage(customer?.id ?: "")
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val currentDestinationId = navController.currentDestination?.id
        if ((currentDestinationId == R.id.menu_shop || currentDestinationId == R.id.menu_profile || currentDestinationId == R.id.viewProductFragment || currentDestinationId == R.id.menu_transactions) && customer !== null) {
            menuInflater.inflate(R.menu.action_menu, menu)
            val cart = menu.findItem(R.id.menu_cart)
            val notif = menu.findItem(R.id.menu_messages)
            BadgeUtils.attachBadgeDrawable(badge, binding.toolbar, cart.itemId)
            BadgeUtils.attachBadgeDrawable(messagesbagde, binding.toolbar, notif.itemId)
            invalidateOptionsMenu()
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cart -> {
                navController.navigate(R.id.menu_cart)
                true
            }
            R.id.menu_messages -> {
                navController.navigate(R.id.menu_messages)
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            authViewModel.getUserByID(uid = it.uid)
        }
    }


    //Count the transactions created or updated today
    fun countTransactionsToday(transactions: List<Transactions>): Int {
     return   transactions.filter { it.transactionDate.isToday() || it.updatedAt.isToday() }.size
    }


    
}
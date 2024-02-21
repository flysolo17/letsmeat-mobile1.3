package com.cjay.letsmeat.views.nav.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cjay.letsmeat.R
import com.cjay.letsmeat.databinding.FragmentCreateAddressFragmemtBinding
import com.cjay.letsmeat.databinding.FragmentEditProfileBinding
import com.cjay.letsmeat.models.customers.Addresses
import com.cjay.letsmeat.models.customers.Contacts
import com.cjay.letsmeat.utils.LoadingDialog
import com.cjay.letsmeat.utils.UiState
import com.cjay.letsmeat.viewmodels.AuthViewModel
import com.cjay.letsmeat.views.auth.ChangePasswordFragmentArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale


class CreateAddressFragmemt : Fragment() {
    private lateinit var _binding : FragmentCreateAddressFragmemtBinding
    private lateinit var _loadingDialog : LoadingDialog
    private val _args by navArgs<CreateAddressFragmemtArgs>()


    private val _authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var _locationRequest : LocationRequest
    private lateinit var _fusedLocationClient: FusedLocationProviderClient
    private val _locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location = p0.lastLocation
            location?.let {
                getLocationInfo(it.latitude,it.longitude)
            }
        }
    }
    private val _locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getLastLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)-> {
                    getLastLocation()
                } else -> {
                Toast.makeText(_binding.root.context,"Permission Denied!", Toast.LENGTH_SHORT).show()
            }
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAddressFragmemtBinding.inflate(inflater,container,false)
        _loadingDialog = LoadingDialog(_binding.root.context)
        val phone =  _args.customer.phone.replace("+63","0")
        _binding.inputContact.setText(phone)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)
        _binding.buttonMyLocation.setOnClickListener {
            if (checkPermission()) {
                getLastLocation()
            }
            requestPermissions()
        }

        _binding.buttonSubmit.setOnClickListener {
            if (!validateInputs()) {
                return@setOnClickListener
            }
            createAddress(provideInputs())
        }
    }

    private fun createAddress(addresses: Addresses) {
        _authViewModel.authRepository.createAddress(_args.customer.id ?: "",addresses) {
            when(it) {
                is UiState.FAILED -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.message,Toast.LENGTH_LONG).show()
                }

                is UiState.LOADING -> {
                    _loadingDialog.showDialog("saving address")
                }
                is UiState.SUCCESS -> {
                    _loadingDialog.closeDialog()
                    Toast.makeText(_binding.root.context,it.data,Toast.LENGTH_LONG).show().also {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        if(!isLocationServiceEnabled()) {
            Toast.makeText(_binding.root.context,"Please enable your location service",Toast.LENGTH_SHORT).show()
            return
        }
        if (checkPermission()) {
            _fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result == null) {
                        getNewLocation()
                        return@addOnCompleteListener
                    }
                    task.result?.let {
                        getLocationInfo(it.latitude,it.longitude)
                    }
                }
            }
        }
        requestPermissions()

    }

    private fun validateInputs() : Boolean {
        //Contact
        val name = _binding.inputName.text.toString()
        val contact = _binding.inputContact.text.toString()
        //address
        val addressLine = _binding.inputRegion.text.toString()
        val postal = _binding.inputPostalCode.text.toString()
        val street = _binding.inputStreet.text.toString()
        if (name.isEmpty()) {
            _binding.layoutName.error ="enter name!"
            return false
        } else if (contact.isEmpty()) {
            _binding.layoutContact.error = "invalid contact!"
            return false
        } else if (contact.length != 11 || !contact.startsWith("09")) {
            _binding.layoutContact.error = "invalid contact!"
            return false
        } else if (addressLine.isEmpty()) {
            _binding.layoutRegion.error = "invalid address line!"
            return false
        } else if (postal.isEmpty()) {
            _binding.layoutPostalCode.error ="Invalid postal code!"
            return false
        } else if (street.isEmpty()) {
            _binding.layoutStreet.error = "Invalid street name!"
            return false
        } else {
            return true
        }
    }

    private fun getNewLocation() {
        _locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(0)
            .setMaxUpdateDelayMillis(2)
            .build()
    }

    private fun getLocationInfo(latitude : Double ,longitude : Double) {
        val geocoder = Geocoder(_binding.root.context, Locale.getDefault())
        val address = geocoder.getFromLocation(latitude,longitude,1)
        val result = StringBuilder();
        result.append(address!![0].adminArea + "\n")
        result.append(address[0].subAdminArea + "\n")
        result.append(address[0].locality + "\n")
        result.append(address[0].featureName)
        _binding.inputRegion.setText(result
        )
    }

    private fun provideInputs() : Addresses {
        //Contact
        val name = _binding.inputName.text.toString()
        val contact = _binding.inputContact.text.toString()
        //address
        val addressLine = _binding.inputRegion.text.toString()
        val postal = _binding.inputPostalCode.text.toString()
        val street = _binding.inputStreet.text.toString()
        return Addresses(Contacts(name,contact),addressLine,postal.toInt(),street)
    }

    private fun checkPermission() : Boolean{
        if (ActivityCompat.checkSelfPermission(_binding.root.context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(_binding.root.context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        _locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    /**
     * Checks if your gps is enabled
     */
    private fun isLocationServiceEnabled() : Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
}
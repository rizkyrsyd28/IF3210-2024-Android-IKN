package com.example.ikn.ui.transaction

import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ikn.R
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionCategory
import com.example.ikn.data.TransactionRepository
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.utils.RandomGenerator
import com.example.ikn.utils.SharedPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

private const val ARG_SHOULD_RANDOM = "should_random"

class NewTransactionFragment : Fragment() {
    private var shouldRandom: Boolean? = null

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository.getInstance(
            AppDatabase.getInstance(requireContext()).transactionDao(),
            SharedPreferencesManager(requireContext())
        )
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise granted
            }

            permission.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Precise granted
            }

            else -> {
                // No permission granted
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        arguments?.let {
            shouldRandom = it.getBoolean(ARG_SHOULD_RANDOM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_new_transaction, container, false)

        val uiModeManager =
            requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val textIdleColor = if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_dark_onTertiaryContainer
            )
        } else {
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_light_onTertiaryContainer
            )
        }
        val textFocusColor = if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_dark_onBackground
            )
        } else {
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_light_onBackground
            )
        }

        // Retrieve all the required form components
        val etNameNewTransaction = rootView.findViewById<EditText>(R.id.etNameNewTransaction)
        val etAmountNewTransaction = rootView.findViewById<EditText>(R.id.etAmountNewTransaction)
        val autoCompleteTextView =
            rootView.findViewById<AutoCompleteTextView>(R.id.autoCategoryNewTransaction)
        val etLocationNewTransaction =
            rootView.findViewById<EditText>(R.id.etLocationNewTransaction)
        val getLocationButton = rootView.findViewById<Button>(R.id.btnGetLocationNewTransaction)
        val saveButton = rootView.findViewById<Button>(R.id.btnSaveNewTransaction)

        // Set the hint text color
        etNameNewTransaction.setHintTextColor(textIdleColor)
        etAmountNewTransaction.setHintTextColor(textIdleColor)
        etLocationNewTransaction.setHintTextColor(textIdleColor)
        autoCompleteTextView.setHintTextColor(textIdleColor)
        etNameNewTransaction.setTextColor(textIdleColor)
        etAmountNewTransaction.setTextColor(textIdleColor)
        etLocationNewTransaction.setTextColor(textIdleColor)
        autoCompleteTextView.setTextColor(textIdleColor)


        // Logic for random generation
        if (shouldRandom == true) {
            val randomGenerator = RandomGenerator()
            etAmountNewTransaction.setText(randomGenerator.getAmount().toString())
            etLocationNewTransaction.setText(randomGenerator.getLocation())

            etAmountNewTransaction.setTextColor(textFocusColor)
            etLocationNewTransaction.setTextColor(textFocusColor)
        }


        // Logic for save button
        var nameInput = etNameNewTransaction.text.toString()
        var amountInput = etAmountNewTransaction.text.toString()
        var locationInput = etLocationNewTransaction.text.toString()
        var categoryInput = autoCompleteTextView.text.toString()

        var isAnyFieldEmpty = nameInput.isBlank() || amountInput.isBlank() ||
                locationInput.isBlank() || categoryInput.isBlank()

        saveButton.isEnabled = !isAnyFieldEmpty
        saveButton.setOnClickListener {

            nameInput = etNameNewTransaction.text.toString()
            amountInput = etAmountNewTransaction.text.toString()
            locationInput = etLocationNewTransaction.text.toString()

            val newTransaction = Transaction(
                name = nameInput,
                amount = amountInput.toInt(),
                location = locationInput,
                category = autoCompleteTextView.text.toString()
            )

            lifecycleScope.launch {
                transactionRepository.insertTransaction(newTransaction)
                parentFragmentManager.popBackStack()
            }

        }
        fun updateSaveButtonState() {
            nameInput = etNameNewTransaction.text.toString()
            amountInput = etAmountNewTransaction.text.toString()
            locationInput = etLocationNewTransaction.text.toString()
            categoryInput = autoCompleteTextView.text.toString()

            isAnyFieldEmpty = nameInput.isBlank() || amountInput.isBlank() ||
                    locationInput.isBlank() || categoryInput.isBlank()

            saveButton.isEnabled = !isAnyFieldEmpty
        }


        // Logic for category dropdown
        val categories = TransactionCategory.entries.toTypedArray()
        val adapterItems =
            TransactionCategoryAdapter(requireContext(), R.layout.item_dropdown, categories)
        autoCompleteTextView.setAdapter(adapterItems)

        autoCompleteTextView.hint = "Category"

        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && autoCompleteTextView.text.isEmpty()) {
                autoCompleteTextView.hint = "Category"
                autoCompleteTextView.showDropDown()
            } else if (!hasFocus && autoCompleteTextView.text.isEmpty()) {
                autoCompleteTextView.hint = "Category"
            } else if (!hasFocus) {
                autoCompleteTextView.hint = " "
            }
        }

        var isItemSelected = false
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedTransactionCategory =
                parent.getItemAtPosition(position) as TransactionCategory
            autoCompleteTextView.setText(selectedTransactionCategory.toString())
            autoCompleteTextView.hint = " "
            isItemSelected = true
            autoCompleteTextView.setTextColor(textFocusColor)
            updateSaveButtonState()
        }
        if (isItemSelected) {
            autoCompleteTextView.setTextColor(textFocusColor)
        }


        // Event listener to change text color
        etNameNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etNameNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etNameNewTransaction.setTextColor(textIdleColor)
                }
                updateSaveButtonState()
            }
        })
        etAmountNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etAmountNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etAmountNewTransaction.setTextColor(textIdleColor)
                }
                updateSaveButtonState()
            }
        })
        etLocationNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etLocationNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etLocationNewTransaction.setTextColor(textIdleColor)
                }
                updateSaveButtonState()
            }
        })


        // Event listener for get location button
        // TODO: Fix this (use service)
        getLocationButton.setOnClickListener {
            Log.i("Location Button", "Location permission given")

            val fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineLocationPermissionGranted) {
                locationPermissionRequest.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }

            if (fineLocationPermissionGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val lat = location.latitude
                            val lon = location.longitude
                            etLocationNewTransaction.setText("($lat, $lon)")
                            Log.i("Location Button", "Current Location: $lat, $lon")
                        } else {
                            Log.i("Location Button", "Null location")


                            val locationCallback = object : LocationCallback() {
                                override fun onLocationResult(p0: LocationResult) {
                                    if (p0.locations.isNotEmpty()) {
                                        val newLocation = p0.locations[0]
                                        val lat = newLocation.latitude
                                        val lon = newLocation.longitude
                                        etLocationNewTransaction.setText("($lat, $lon)")
                                        Log.i("Location Button", "Current Location: $lat, $lon")
                                    }
                                }
                            }

                            val locationRequest = LocationRequest.Builder(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                10000
                            ).build()

                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            )

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Location Button", exception.message.toString())
                    }
            }

        }

        return rootView
    }

    override fun onPause() {
        super.onPause()
        parentFragmentManager.saveBackStack("new_transaction")
        Log.d("TransactionFragmentManager", "Back stack entry count: ${parentFragmentManager.backStackEntryCount}")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param shouldRandom Indicates whether or not to generate
         * a random location and amount.
         * @return A new instance of fragment NewTransactionFragment.
         */
        @JvmStatic
        fun newInstance(shouldRandom: Boolean = false) =
            NewTransactionFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_SHOULD_RANDOM, shouldRandom)
                }
            }
    }
}
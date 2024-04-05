package com.example.ikn.ui.Scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ikn.MainActivity
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository
import com.example.ikn.databinding.FragmentScanBinding
import com.example.ikn.model.response.item.Items
import com.example.ikn.service.network.NetworkBroadcastReceiver
import com.example.ikn.ui.transaction.Transaction
import com.example.ikn.utils.SharedPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch


class ScanFragment : Fragment() {
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var networkReceiver: NetworkBroadcastReceiver
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository.getInstance(
            AppDatabase.getInstance(requireContext()).transactionDao(),
            SharedPreferencesManager(requireContext())
        )
    }

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var inputLocation : String = ""

    companion object {
        private const val TAG = "CameraFragment"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root

        scanViewModel = ViewModelProvider(this, ScanViewModelFactory())[ScanViewModel::class.java]
        scanViewModel.bill = MutableLiveData()
        scanViewModel.bill.observe(viewLifecycleOwner) { response ->
            Log.e(TAG, response.toString())
            binding.progressBarCyclic.visibility = View.GONE
            if (response != null) {
                createDialog(response.items)
            } else {
                createDialog(null)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return view
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!requireActivity().intent.extras?.getBoolean("status")!!) {
            binding.snapButton.isClickable = false
            binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
            binding.uploadButton.isClickable = false
            binding.uploadButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
            return
        }

        pickImage()
        if (cameraPermissionsGranted()) {
            Log.i("DEBUG", "permission granted")
            startCamera()
            binding.snapButton.setOnClickListener {
                captureImage()
            }
            getLocation()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            getLocation()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        networkReceiver = NetworkBroadcastReceiver()
        networkReceiver.setConnectedHandler {
            Log.e(TAG, "Connected Handler")
            if (requireActivity().intent.extras?.getBoolean("status")!!) {
                binding.snapButton.isClickable = true
                binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0F806"))
                binding.uploadButton.isClickable = true
                binding.uploadButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0F806"))
            }

        }
        networkReceiver.setDisconnectedHandler {
            Log.e(TAG, "Diconnected Handler")
            binding.snapButton.isClickable = false
            binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
            binding.uploadButton.isClickable = false
            binding.uploadButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
        }
        requireActivity().registerReceiver(networkReceiver, IntentFilter("NETWORK_STATUS"))

    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().unregisterReceiver(networkReceiver)
    }

    private fun cameraPermissionsGranted() : Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun locationPermissionsGranted() : Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("DEBUG", "permission granted")
            startCamera()
            binding.snapButton.setOnClickListener {
                captureImage()
            }
        } else {
            Toast.makeText(requireContext(), "Camera permission is not granted.", Toast.LENGTH_SHORT).show()
            binding.snapButton.isClickable = false
            binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
        }
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        Log.e(TAG, locationPermissionsGranted().toString())
        if (locationPermissionsGranted()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    inputLocation = "($lat, $lon)"
                    Log.i("Location scan", "Current Location: $lat, $lon")
                } else {
                    Log.i("Location scan", "Null location")
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            if (p0.locations.isNotEmpty()) {
                                val newLocation = p0.locations[0]
                                val lat = newLocation.latitude
                                val lon = newLocation.longitude
                                inputLocation = "($lat, $lon)"
                                Log.i("Location scan", "Current Location: $lat, $lon")
                            }
                        }
                    }

                    val locationRequest = LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        0
                    ).build()

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                }
            }.addOnFailureListener { exception ->
                Log.i("Location Button", exception.message.toString())
            }
        }
    }


    private fun bindCameraUseCases() {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imageCapture = ImageCapture.Builder()
            .build()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewCamera.surfaceProvider)
        }

        try {
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error binding camera use cases", e)
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), object: ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val imageFile = scanViewModel.createFileFromProxyImg(image, requireContext().cacheDir)
                image.close()

                scanViewModel.doPostBill(imageFile, requireContext())
                binding.progressBarCyclic.visibility = View.VISIBLE
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Error capturing image: ${exception.message}", exception)
                Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun pickImage() {
        pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                val file = kotlin.io.path.createTempFile().toFile()
                uri.let { requireContext().contentResolver.openInputStream(it) }.use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }

                scanViewModel.doPostBill(file, requireContext())
                binding.progressBarCyclic.visibility = View.VISIBLE
                Log.d("PhotoPicker", "Selected URI: ${uri.path}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.uploadButton.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    fun createDialog(billItems: Items?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val messageBuilder = StringBuilder()
        val titleBuilder = StringBuilder()
        val posBtnBuilder = StringBuilder()
        val negBtnBuilder = StringBuilder()

        val transactionName = StringBuilder()
        var itemAmount : Double = 0.0

        if (billItems != null) {
            titleBuilder.append("Scanned Transactions")
            messageBuilder.append("Do you want to save this transactions?\n\n")
            posBtnBuilder.append("Save Transaction")
            negBtnBuilder.append("Cancel")
            for ((index, item) in billItems.items.withIndex()) {
                val itemNumber = index + 1
                messageBuilder.append("Item $itemNumber\n")
                messageBuilder.append("• Name: ${item.name}\n")
                messageBuilder.append("• Price: ${item.price}\n")
                messageBuilder.append("• Quantity: ${item.qty}\n\n")

                transactionName.append(item.name)
                if (index !== billItems.items.size - 1) {
                    transactionName.append(" - ")
                }
                itemAmount += item.qty * item.price
            }
        } else {
            titleBuilder.append("An Error Occurred")
            messageBuilder.append("An error has occurred")
            posBtnBuilder.append("Back")
            negBtnBuilder.append("")
            negBtnBuilder.append("")
        }

        builder
            .setMessage(messageBuilder.toString())
            .setTitle(titleBuilder.toString())
            .setPositiveButton(posBtnBuilder.toString()) { dialog, which ->
                if (billItems != null) {
                    val newTransaction = Transaction(
                        name = transactionName.toString(),
                        amount = itemAmount.toInt(),
                        location = inputLocation,
                        category = "Pengeluaran"
                    )
                    lifecycleScope.launch {
                        transactionRepository.insertTransaction(newTransaction)
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton(negBtnBuilder.toString()) { dialog, which ->
                // Do something else.
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

//    private fun

    override fun onDestroyView() {
        super.onDestroyView()
        cameraProvider?.unbindAll()
        _binding = null
        scanViewModel.bill = MutableLiveData()
    }
}
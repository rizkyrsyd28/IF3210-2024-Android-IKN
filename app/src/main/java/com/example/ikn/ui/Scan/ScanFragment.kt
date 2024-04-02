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
import com.example.ikn.MainActivity
import com.example.ikn.databinding.FragmentScanBinding
import com.example.ikn.model.response.item.Items
import com.example.ikn.service.network.NetworkBroadcastReceiver


class ScanFragment : Fragment() {
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var networkReceiver: NetworkBroadcastReceiver

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "CameraFragment"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickImage()
        if (allPermissionsGranted()) {
            Log.i("DEBUG", "permission granted")
            startCamera()
            binding.snapButton.setOnClickListener {
                captureImage()
            }
        } else {
            val requestPermissionLauncher = registerForActivityResult(
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
            }
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        networkReceiver = NetworkBroadcastReceiver()
        requireActivity().registerReceiver(networkReceiver, IntentFilter("NETWORK_STATUS"))

        networkReceiver.setConnectedHandler {
            Log.e(TAG, "Connected Handler")
            binding.snapButton.isClickable = true
            binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0F806"))
            binding.uploadButton.isClickable = true
            binding.uploadButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E0F806"))

        }
        networkReceiver.setDisconnectedHandler {
            Log.e(TAG, "Diconnected Handler")
            binding.snapButton.isClickable = false
            binding.snapButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
            binding.uploadButton.isClickable = false
            binding.uploadButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#61FFEB3B"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().unregisterReceiver(networkReceiver)
    }

    private fun allPermissionsGranted() : Boolean =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
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
                // Do something.
                dialog.dismiss()
            }
            .setNegativeButton(negBtnBuilder.toString()) { dialog, which ->
                // Do something else.
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraProvider?.unbindAll()
        _binding = null
        scanViewModel.bill = MutableLiveData()
    }
}
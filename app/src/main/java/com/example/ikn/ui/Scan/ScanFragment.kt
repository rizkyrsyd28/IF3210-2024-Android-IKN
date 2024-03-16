package com.example.ikn.ui.Scan

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.lifecycle.ViewModelProvider
import com.example.ikn.databinding.FragmentScanBinding
import java.io.File

class ScanFragment : Fragment() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>;
    private lateinit var scanViewModel: ScanViewModel

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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root

        scanViewModel = ViewModelProvider(this, ScanViewModelFactory())[ScanViewModel::class.java]
        scanViewModel.bill.observe(viewLifecycleOwner) { response ->
            val body = response.body()
            Log.i(TAG, response.toString())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            pickImage()
            startCamera()
            binding.snapButton.setOnClickListener {
                captureImage()
            }
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.i("DEBUG", "permission granted")
                    pickImage()
                    startCamera()
                    binding.snapButton.setOnClickListener {
                        captureImage()
                    }
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                    Log.i("DEBUG", "permission denied")
                }
            }
        }
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
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
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
                val bitmap = image.toBitmap()
                val imageFile = scanViewModel.createFileFromProxyImg(image, requireContext().cacheDir)
                image.close()

                scanViewModel.doPostBill(imageFile)

                // Display the captured image to the user
                binding.mainCameraFrame.setImageBitmap(bitmap)
                binding.mainCameraFrame.rotation = image.imageInfo.rotationDegrees.toFloat()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Error capturing image: ${exception.message}", exception)
                Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun pickImage() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val file = File(uri.path);
                binding.mainCameraFrame.setImageURI(uri)
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.uploadButton.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        cameraProvider.unbindAll()
        _binding = null
    }
}
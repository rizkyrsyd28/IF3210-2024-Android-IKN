package com.example.ikn.ui.Scan

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ikn.model.response.BillResponse
import com.example.ikn.repository.PreferenceRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class ScanViewModel(): ViewModel() {
    var bill: MutableLiveData<BillResponse?> = MutableLiveData()
    private val repository = Repository()

    companion object {
        private const val TAG = "CameraViewModel"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    fun getBill(): LiveData<BillResponse?> {
        return bill
    }

    fun createFileFromProxyImg(image: ImageProxy, cacheDir: File): File {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())
        val tempPath = kotlin.io.path.createTempFile(cacheDir.toPath(), "IMG_$timeStamp", ".jpg")
        val tempFile = tempPath.toFile()

        FileOutputStream(tempFile).use { outputStream ->
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            outputStream.write(bytes)
        }
        return tempFile
    }

    fun createFileFromUri(uri: Uri) : File {
        return File(uri.path)
    }

    fun doPostBill(file: File, context: Context) = viewModelScope.launch {
        try {
            val sharedPref = PreferenceRepository(SharedPreferencesManager(context))
            val token = sharedPref.getToken()
            Log.e("TOKEN", token)

            var fetchedBill : BillResponse? = null
            if (token.isNotBlank()) {
                val response = repository.postBill(file, token)
                fetchedBill = response.body()
                Log.e("PostBill", fetchedBill.toString())
            }
            bill.value = fetchedBill
        } catch (e: Exception) {
            Log.e("ScanViewModel", e.toString())
            bill.value = null
            e.printStackTrace()
        }

    }



}
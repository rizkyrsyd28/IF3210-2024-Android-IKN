package com.example.ikn.ui.Scan

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ikn.model.response.BillResponse
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.launch

class ScanViewModel(): ViewModel() {
    private val bill: MutableLiveData<BillResponse> = MutableLiveData()
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
            val sharedPref = SharedPreferencesManager(context)
            val token = sharedPref.get("TOKEN")

            var fetchedBill : BillResponse? = null
            if (!token.isNullOrBlank()) {
                val response = repository.postBill(file, token)
                fetchedBill = response.body()
                println(response.body())
            }
            bill.value = fetchedBill!!;
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
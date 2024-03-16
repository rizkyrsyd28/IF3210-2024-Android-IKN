package com.example.ikn.ui.Scan

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
import kotlinx.coroutines.launch
import retrofit2.Response

class ScanViewModel(): ViewModel() {
    private val bill: MutableLiveData<Response<BillResponse>> = MutableLiveData()
    private val repository = Repository()

    companion object {
        private const val TAG = "CameraViewModel"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    fun getBill(): LiveData<Response<BillResponse>> {
        return bill
    }

    fun createFileFromProxyImg(image: ImageProxy, cacheDir: File): File {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())
        val tempFile = File.createTempFile("IMG_$timeStamp", ".jpg", cacheDir)

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

    fun doPostBill(file: File) = viewModelScope.launch {
        try {
//            TODO("change hardcoded token")
            val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaW0iOiIxMzUyMTEwOSIsImlhdCI6MTcxMDU4Nzk0OCwiZXhwIjoxNzEwNTg4MjQ4fQ.q4HG8Ed92q_oYQTg5l_lhOEVBZQDWdDP3EHW4ncXsYs"
            val response = repository.postBill(file, token)
            bill.value = response

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
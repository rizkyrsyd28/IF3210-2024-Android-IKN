package com.example.ikn.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class Converter {
    companion object {
        fun FileToMultipart(file: File): MultipartBody.Part {
            val body = RequestBody.create(MediaType.parse("image/*"), file)
            return MultipartBody.Part.createFormData("file", file.name, body)
        }
    }
}
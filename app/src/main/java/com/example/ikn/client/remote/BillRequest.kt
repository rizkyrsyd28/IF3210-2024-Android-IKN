package com.example.ikn.client.remote

import com.example.ikn.model.response.BillResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BillRequest {
    @Multipart
    @POST("bill/upload")
    suspend fun post(@Part file: MultipartBody.Part, @Header("Authorization") auth: String) : Response<BillResponse>
}
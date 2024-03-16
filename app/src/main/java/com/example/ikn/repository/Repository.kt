package com.example.ikn.repository

import com.example.ikn.client.remote.HttpClient
import com.example.ikn.model.request.LoginRequest
import com.example.ikn.model.response.BillResponse
import com.example.ikn.model.response.LoginResponse
import com.example.ikn.model.response.TokenResponse
import com.example.ikn.utils.Converter
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.File

class Repository {
    suspend fun postBill(file: File, token: String): Response<BillResponse> {
        return HttpClient.billInstance.post(Converter.FileToMultipart(file), "Bearer $token")
    }

    suspend fun postLogin(email: String, password: String): Response<LoginResponse> {
        return HttpClient.authInstance.postLogin(LoginRequest(email, password))
    }

    suspend fun postToken(token: String): Response<TokenResponse> {
        return HttpClient.authInstance.postToken("Bearer $token")
    }
}
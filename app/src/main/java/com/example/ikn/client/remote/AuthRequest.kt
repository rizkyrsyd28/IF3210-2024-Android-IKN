package com.example.ikn.client.remote

import com.example.ikn.model.response.LoginResponse
import com.example.ikn.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthRequest {
    @POST("auth/login")
    suspend fun postLogin(@Path("email") email: String, @Path("password") password: String): Response<LoginResponse>

    @POST("auth/token")
    suspend fun postToken(@Header("Authorization") auth: String) : Response<TokenResponse>
}
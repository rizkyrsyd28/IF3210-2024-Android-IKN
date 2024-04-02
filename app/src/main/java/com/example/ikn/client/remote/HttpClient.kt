package com.example.ikn.client.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpClient {

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build();

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val billInstance: BillRequest by lazy {
        retrofit.create(BillRequest::class.java)
    }

    val authInstance: AuthRequest by lazy {
        retrofit.create(AuthRequest::class.java)
    }
}
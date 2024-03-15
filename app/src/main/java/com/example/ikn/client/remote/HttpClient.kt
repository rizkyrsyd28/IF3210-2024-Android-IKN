package com.example.ikn.client.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/api")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//    val postBill() :  {
//
//    }
}
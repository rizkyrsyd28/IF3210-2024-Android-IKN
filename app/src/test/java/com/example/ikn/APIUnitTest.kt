package com.example.ikn

import android.media.Image
import com.example.ikn.client.remote.HttpClient
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class APIUnitTest {
    private lateinit var client : HttpClient
    @Before
    fun instanceClient(){
        client = HttpClient
//        try {
//            client = HttpClient
//        } catch (err: Error) {
//            println(err)
//        }
    }

    fun post(file: File) {
        client.postBill(file)
    }

    @Test
    fun uploadAPI() {
        val img = File("C:/Users/LENOVO/Pictures/Abang2an.png")
        client.postBill(img)
    }
}
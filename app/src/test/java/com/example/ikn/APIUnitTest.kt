package com.example.ikn

import com.example.ikn.repository.Repository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.io.File

class APIUnitTest {
    private var token : String = ""

    @Test
    fun loginAPI() {
        runBlocking {
            val repo = Repository()
            val data = repo.postLogin("13521109@std.stei.itb.ac.id", "password_13521109")
            println("${data.body()?.token}")
            token = data.body()?.token as String
        }
    }

    @Test
    fun uploadAPI() {
        runBlocking {
            val repo = Repository()
            val file = File("C:/Users/sadda/OneDrive - Institut Teknologi Bandung/Pictures/Screenshots/Screenshot 2024-03-10 172607.png")
            val res = repo.postBill(file, token)
            println("data - ${res.body()}")
        }
    }

    @Test
    fun tokenAPI() {
        runBlocking {
            val repo = Repository()
            val data = repo.postToken(token)
            println("Tokenz ${data.body()}")
        }
    }
}
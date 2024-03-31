package com.example.ikn

import org.junit.Test

import org.junit.Assert.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun email_validation() {
        val email = "13521109std.stei.itb.ac.id"
        val emailRegex = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}"
        assertEquals(email.matches(emailRegex.toRegex()), false)
    }

    @Test
    fun check_time() {
        val expTimestamp = 1711855831L  // Waktu kedaluwarsa dalam UNIX timestamp
        val currentTime = Instant.now().epochSecond  // Waktu sekarang dalam UNIX timestamp
        println("Current $currentTime ")

        val differenceInSeconds = expTimestamp - currentTime

        println("Selisih waktu antara sekarang dan waktu kedaluwarsa: $differenceInSeconds detik")
    }
}
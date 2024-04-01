package com.example.ikn.utils

import kotlin.math.max
import kotlin.random.Random

class RandomGenerator {
    fun getAmount () : Int {
        return Random.nextInt(minAmount, maxAmount)
    }
    fun getLocation () : String {
        val lat = Random.nextDouble(minLat, maxLat)
        val lon = Random.nextDouble(minLon, maxLon)

        return "($lat,$lon)"
    }
    fun getCategory () : String {
        return if (Random.nextBoolean()) "Pemasukan" else "Pengeluaran"
    }

    companion object {
        private const val maxAmount = 5000000
        private const val minAmount = 10000
        private const val maxLat = 5.0
        private const val minLat = -11.0
        private const val maxLon = 141.0
        private const val minLon = 95.0
    }
}
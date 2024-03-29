package com.example.ikn.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: Int,

    val name: String,
    val category: TransactionCategory,
    val amount: Int,
    val location: String,
    val date: Calendar = Calendar.getInstance()
)

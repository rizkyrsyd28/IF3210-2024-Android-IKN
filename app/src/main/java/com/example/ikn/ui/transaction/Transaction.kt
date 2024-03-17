package com.example.ikn.ui.transaction

data class Transaction(
    val date: String,
    val category: String,
    val name: String,
    val amount: Int,
    val location: String,
)

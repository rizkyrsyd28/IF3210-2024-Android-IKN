package com.example.ikn.ui.transaction

data class Transaction(
    val id: Int = 0,
    val date: String = "",
    val category: String,
    val name: String,
    val amount: Int,
    val location: String,
)

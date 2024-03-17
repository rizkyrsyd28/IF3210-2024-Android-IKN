package com.example.ikn.ui.transaction

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TransactionHistory(
    val history: List<Transaction> = listOf()
)

class TransactionViewModel : ViewModel() {

    private val _transactionHistory = MutableStateFlow(TransactionHistory())
    val transactionHistory: StateFlow<TransactionHistory> = _transactionHistory.asStateFlow()
}
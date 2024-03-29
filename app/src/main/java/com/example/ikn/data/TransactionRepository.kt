package com.example.ikn.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.ikn.ui.transaction.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    private val dateFormat = SimpleDateFormat("dd MMM yyy", Locale.US)
    val transactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
        .map { transactions ->
            transactions.map { transaction ->
                Transaction(
                    date = dateFormat.format(transaction.date.time),
                    category = transaction.category.toString(),
                    name = transaction.name,
                    amount = transaction.amount,
                    location = transaction.location
                )
            }
        }

    companion object {

        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(transactionDao: TransactionDao) =
            instance ?: synchronized(this) {
                instance ?: TransactionRepository(transactionDao).also { instance = it }
            }
    }
}





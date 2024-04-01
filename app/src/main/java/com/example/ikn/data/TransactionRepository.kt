package com.example.ikn.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.ui.transaction.Transaction
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val sharedPreferencesManager: SharedPreferencesManager,
) {

    private val dateFormat = SimpleDateFormat("dd MMM yyy", Locale.US)
    private val dateFormFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val transactions: Flow<List<Transaction>> = transactionDao.getAllTransactions(
        sharedPreferencesManager.get(PreferenceRepository.emailKey).toString().substringBefore("@")
    )
        .map { transactions ->
            transactions.map { transaction ->
                Transaction(
                    id = transaction.id,
                    date = dateFormat.format(transaction.date.time),
                    category = transaction.category.toString(),
                    name = transaction.name,
                    amount = transaction.amount,
                    location = transaction.location
                )
            }
        }

    suspend fun insertTransaction(transaction: Transaction) {

        val newTransaction = com.example.ikn.data.Transaction(
            name = transaction.name,
            amount = transaction.amount,
            location = transaction.location,
            category = TransactionCategory.valueOf(transaction.category),
            nim = sharedPreferencesManager.get(PreferenceRepository.emailKey).toString().substringBefore("@")
        )
        transactionDao.insertTransaction(
            newTransaction
        )

    }

    suspend fun getTransactionById(transactionId: Int): Transaction? {
        val transaction = transactionDao.getTransactionById(transactionId)

        return if (transaction == null) {
            null
        } else {
            Transaction(
                id = transactionId,
                date = dateFormat.format(transaction.date.time),
                category = transaction.category.toString(),
                name = transaction.name,
                amount = transaction.amount,
                location = transaction.location
            )
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {

        val calendar = Calendar.getInstance()
        calendar.time = dateFormFormat.parse(transaction.date)!!

        val newTransaction = com.example.ikn.data.Transaction(
            id = transaction.id,
            name = transaction.name,
            date = calendar,
            amount = transaction.amount,
            location = transaction.location,
            category = TransactionCategory.valueOf(transaction.category),
            nim = sharedPreferencesManager.get(PreferenceRepository.emailKey).toString().substringBefore("@")
        )
        transactionDao.updateTransaction(
            newTransaction
        )
    }

    suspend fun deleteTransaction(transactionId: Int) =
        transactionDao.deleteTransaction(transactionId)

    companion object {

        @Volatile
        private var instance: TransactionRepository? = null

        fun getInstance(
            transactionDao: TransactionDao,
            sharedPreferencesManager: SharedPreferencesManager,
        ) =
            instance ?: synchronized(this) {
                instance ?: TransactionRepository(
                    transactionDao,
                    sharedPreferencesManager
                ).also { instance = it }
            }
    }
}





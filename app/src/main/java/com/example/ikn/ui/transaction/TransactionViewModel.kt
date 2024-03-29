package com.example.ikn.ui.transaction

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ikn.MainActivity
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository


class TransactionViewModel(
    transactionRepository: TransactionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //    private val _transactionHistory = MutableLiveData(TransactionHistory())
//    val transactionHistory: LiveData<TransactionHistory> = _transactionHistory.asStateFlow()
    val transactions: LiveData<List<Transaction>> = transactionRepository.transactions.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application context from extras
                val applicationContext = checkNotNull(extras[APPLICATION_KEY] as? Context)
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return TransactionViewModel(
                    TransactionRepository.getInstance(
                        AppDatabase.getInstance(applicationContext).transactionDao()
                    ),
                    savedStateHandle
                ) as T
            }
        }
    }
}
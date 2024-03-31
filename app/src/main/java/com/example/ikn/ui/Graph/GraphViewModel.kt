package com.example.ikn.ui.Graph
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionCategory
import com.example.ikn.data.TransactionRepository
import com.example.ikn.ui.transaction.Transaction

class GraphViewModel(transactionRepository: TransactionRepository) : ViewModel() {
    val transactions: LiveData<List<Transaction>> = transactionRepository.transactions.asLiveData();

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application context from extras
                val applicationContext = checkNotNull(extras[APPLICATION_KEY] as? Context)
                return GraphViewModel(
                    TransactionRepository.getInstance(
                        AppDatabase.getInstance(applicationContext).transactionDao()
                    )
                ) as T
            }
        }
    }

    fun getGraphData(transactions: List<Transaction>) : GraphData {
        var income : Float = 0f;
        var expense : Float = 0f;
        for (t in transactions) {
            if (t.category === TransactionCategory.Pengeluaran.toString()) {
                expense += t.amount;
            } else {
                income += t.amount;
            }
        }
        val total : Float = income + expense;
        val incomePrcnt : Float = income / total;
        val expensePrcnt : Float = expense / total;
        val graphData = GraphData(income, expense, total, incomePrcnt, expensePrcnt)
        return graphData;
    }
}


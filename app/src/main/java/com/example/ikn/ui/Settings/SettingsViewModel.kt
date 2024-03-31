package com.example.ikn.ui.Settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository
import com.example.ikn.ui.transaction.Transaction
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SettingsViewModel(transactionRepository: TransactionRepository) : ViewModel() {
    val transactions: LiveData<List<Transaction>> = transactionRepository.transactions.asLiveData();

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application context from extras
                val applicationContext = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? Context)
                return SettingsViewModel(
                    TransactionRepository.getInstance(
                        AppDatabase.getInstance(applicationContext).transactionDao()
                    )
                ) as T
            }
        }
    }

    fun createExcel(path: String, isXlsx: Boolean) : String {
        val workbook = WorkbookFactory.create(true)
        val sheet = workbook.createSheet("Sheet1")

        // Create header
        val headerRow = sheet.createRow(0)
        val headers = arrayOf("Title", "Category", "Amount", "Location", "Date")
        for (i in headers.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(headers[i])
        }

        val transactionsData = transactions.value

        if (transactionsData == null) return "";


        for (i in transactionsData.indices) {
            val row = sheet.createRow(i + 1)
            val transaction = transactionsData[i]

            val titleCell = row.createCell(0)
            titleCell.setCellValue(transaction.name)

            val categoryCell = row.createCell(1)
            categoryCell.setCellValue(transaction.category)

            val amountCell = row.createCell(2)
            amountCell.setCellValue(transaction.amount.toDouble())

            val locationCell = row.createCell(3)
            locationCell.setCellValue(transaction.location)

            val dateCell = row.createCell(4)
            dateCell.setCellValue(transaction.date)
        }

        // Save the workbook to a file
        var extension = ".xls"
        if (isXlsx) extension = ".xlsx"

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("ss-mm-HH:dd-MM-yy")
        val formattedDateTime = currentDateTime.format(formatter)

        val filename = "TransactionsData$formattedDateTime";
        val fullPath = "$path/$filename$extension"

        val fileOut = FileOutputStream(fullPath)
        workbook.write(fileOut)
        fileOut.close()

        return fullPath;
    }
}
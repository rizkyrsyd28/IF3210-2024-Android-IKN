package com.example.ikn.service.file

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.ui.transaction.Transaction
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class FileService(): Service() {
    private lateinit var repo : TransactionRepository
    private lateinit var prefRepo : PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        repo = TransactionRepository.getInstance(
            AppDatabase.getInstance(applicationContext).transactionDao(),
            SharedPreferencesManager(applicationContext)
        )
        prefRepo = PreferenceRepository.getInstance((SharedPreferencesManager(applicationContext)))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Destroy File Service")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.e(TAG, "Intent ${intent?.action}")

        if (intent?.action == null || intent.extras == null) return START_STICKY

        val dir = intent.extras!!.getString("DIR")!!
        val isXlsx = intent.extras!!.getBoolean("XLSX")
        var isSend = false

        when (intent.action) {
            "com.example.ikn.CREATE_FILE" -> isSend = false
            "com.example.ikn.SEND_FILE" -> isSend = true
        }

        doWork(dir, isXlsx, isSend)

        return START_STICKY
    }

    private fun doWork(dir: String, isXlsx: Boolean, isSend: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = getTransactions()

            if (transactions.isEmpty()) {
                Log.w(TAG, "Transactions is Empty")
                return@launch
            }

            val workbook = WorkbookFactory.create(true)
            val sheet = workbook.createSheet("Sheet1")

            // Create header
            val headerRow = sheet.createRow(0)
            val headers = arrayOf("Title", "Category", "Amount", "Location", "Date")

            // Style it
            val headerCellStyle: CellStyle = workbook.createCellStyle()
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index)
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
            val font: Font = workbook.createFont()
            font.setBold(true)
            font.color = IndexedColors.WHITE.index
            headerCellStyle.setFont(font)

            for (i in headers.indices) {
                val cell = headerRow.createCell(i)
                cell.setCellValue(headers[i])
                cell.cellStyle = headerCellStyle
                sheet.setColumnWidth(i, 15 * 256)
            }

            for (i in transactions.indices) {
                val row = sheet.createRow(i + 1)
                val transaction = transactions[i]

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
            val formatter = DateTimeFormatter.ofPattern("ss-mm-HH.dd-MM-yy")
            val formattedDateTime = currentDateTime.format(formatter)

            val filename = "TransactionsData$formattedDateTime";

            val outputFile = File("$dir/$filename$extension")
            workbook.write(outputFile.outputStream())
            workbook.close()

            Log.e(TAG, "dir = ${outputFile.path}")

            outputBroadcast(outputFile.path, isSend)

            delay(TimeUnit.SECONDS.toMillis(10))

            stopSelf()
        }
    }
    private fun outputBroadcast(dir: String, isSend: Boolean) {
        val action =
            if (isSend) "com.example.ikn.OPEN_EMAIL"
            else "com.example.ikn.OPEN_FILE"

        Log.w(TAG, "Action - $action")

        val intent = Intent(action).apply {
            putExtra("DIR", dir)
            if (isSend) putExtra("SEND_TO", prefRepo.getSignInInfo().first)
        }
        sendBroadcast(intent)
    }
    /* TODO: Bikinin Get All untuk Transaction (ZAKI) */
    private fun getTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                id = 1,
                date = "01 Apr 2024",
                category = "Pengeluaran",
                name = "Kebab Bossman",
                amount = 30000,
                location = "Sekeloa"
            ),
            Transaction(
                id = 5,
                date = "01 Apr 2024",
                category = "Pengeluaran",
                name = "PS",
                amount = 17500,
                location = "Cisitu"
            ),
            Transaction(
                id = 8,
                date = "01 Apr 2024",
                category = "Pengeluaran",
                name = "Futsal",
                amount = 75000,
                location = "YPKP"
            )
        )
    }

    companion object {
        const val TAG = "[FILE SERVICE]"
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

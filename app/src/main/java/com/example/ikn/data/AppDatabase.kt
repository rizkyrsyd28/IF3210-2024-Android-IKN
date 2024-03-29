package com.example.ikn.data

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, CategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bondoman.db"
                )
                .addCallback(
                    object: Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context)
                                    .transactionDao()
                                    .insertAllTransactions(prepopulateData)
                            }
                        }
                    }
                )
                .build()
        }

        val prepopulateData = listOf(
            Transaction(1, "Kebab Bossman", TransactionCategory.Pengeluaran, 30000, "Sekeloa"),
            Transaction(2, "Warkop 99", TransactionCategory.Pemasukan, 15000, "Tubis"),
            Transaction(3, "Project Benny", TransactionCategory.Pemasukan, 40000, "Cisitu"),
            Transaction(4, "Bensin Shidqi", TransactionCategory.Pemasukan, 65000, "Parkir Sipil"),
            Transaction(5, "PS", TransactionCategory.Pengeluaran, 17500, "Cisitu"),
            Transaction(6, "Crisbar", TransactionCategory.Pengeluaran, 32000, "Cisitu"),
            Transaction(7, "Bensin Shidqi", TransactionCategory.Pengeluaran, 50000, "Parkir Sipil"),
            Transaction(8, "Futsal", TransactionCategory.Pengeluaran, 75000, "YPKP"),
        )
    }
}
package com.example.ikn.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class], version = 2, exportSchema = false)
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
                .addMigrations(MIGRATION_1_2)
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

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Perform migration SQL statements here
        db.execSQL(
            "CREATE TABLE transactions_temp (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "    name TEXT NOT NULL DEFAULT undefined," +
                    "    date INTEGER NOT NULL DEFAULT undefined," +
                    "    amount INTEGER NOT NULL DEFAULT undefined," +
                    "    location TEXT NOT NULL DEFAULT undefined," +
                    "    category TEXT NOT NULL DEFAULT undefined" +
                    ");"
        )

        // Copy data from the old table to the temporary table
        db.execSQL("INSERT INTO transactions_temp (name, date, amount, location, category) SELECT name, date, amount, location, category FROM transactions;")

        // Drop the old table
        db.execSQL("DROP TABLE transactions;")

        // Rename the temporary table to the original table name
        db.execSQL("ALTER TABLE transactions_temp RENAME TO transactions;")
    }
}

// TODO: Create migration 2 -> 3 (adding user email to data.transaction)
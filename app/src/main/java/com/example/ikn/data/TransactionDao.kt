package com.example.ikn.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(vararg transaction: Transaction)

    @Insert
    fun insertAllTransactions(transactions: List<Transaction>)

    @Update
    fun updateTransaction(vararg transaction: Transaction)

    @Delete
    fun deleteTransaction(vararg transaction: Transaction)
}
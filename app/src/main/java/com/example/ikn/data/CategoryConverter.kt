package com.example.ikn.data

import androidx.room.TypeConverter

class CategoryConverter {

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTransactionCategory(category: String) : TransactionCategory {
        return TransactionCategory.valueOf(category)
    }
}
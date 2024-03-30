package com.example.ikn.ui.transaction

import android.content.Context
import android.widget.ArrayAdapter
import com.example.ikn.data.TransactionCategory

class TransactionCategoryAdapter(
    context: Context,
    resource: Int,
    private val categories: Array<TransactionCategory>
) : ArrayAdapter<TransactionCategory>(context, resource, categories) {

    override fun getCount(): Int {
        return categories.size
    }

    override fun getItem(position: Int): TransactionCategory {
        return categories[position]
    }

}
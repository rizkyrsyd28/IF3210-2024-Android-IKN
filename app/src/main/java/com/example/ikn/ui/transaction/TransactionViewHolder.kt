package com.example.ikn.ui.transaction

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ikn.R
import java.text.NumberFormat
import java.util.Locale

class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val nameTextView: TextView
    private val dateTextView: TextView
    private val categoryTextView: TextView
    private val amountTextView: TextView
    private val locationTextView: TextView

    init {
        nameTextView = view.findViewById(R.id.tvTransactionName)
        dateTextView = view.findViewById(R.id.tvDate)
        categoryTextView = view.findViewById(R.id.tvCategory)
        amountTextView = view.findViewById(R.id.tvAmount)
        locationTextView = view.findViewById(R.id.tvLocation)
    }

    fun bind(transaction: Transaction) {
        nameTextView.text = transaction.name
        amountTextView.text = NumberFormat
            .getNumberInstance(Locale.US)
            .format(transaction.amount)
        dateTextView.text = transaction.date
        locationTextView.text = transaction.location
        categoryTextView.text = transaction.category
    }
}
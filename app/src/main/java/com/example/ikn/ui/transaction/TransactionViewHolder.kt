package com.example.ikn.ui.transaction

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ikn.MainActivity
import com.example.ikn.R
import java.text.NumberFormat
import java.util.Locale

class TransactionViewHolder(
    view: View,
    private val itemClickListener: TransactionAdapter.OnTransactionItemClickListener?,
    private val itemLongClickListener: TransactionAdapter.OnTransactionItemLongClickListener?,
) : RecyclerView.ViewHolder(view) {
    private var transactionId: Int = -1;
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

        itemView.setOnLongClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemLongClickListener?.onItemLongClick(transactionId)
                true
            } else {
                false
            }
        }

        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val locationString = locationTextView.text.toString()
                itemClickListener?.onItemClick(locationString)
                Log.d("MAP Intent", "location string: $locationTextView.text.toString()")
            }
        }

    }

    fun bind(transaction: Transaction) {
        transactionId = transaction.id
        nameTextView.text = transaction.name
        amountTextView.text = NumberFormat
            .getNumberInstance(Locale.US)
            .format(transaction.amount)
        dateTextView.text = transaction.date
        locationTextView.text = transaction.location
        categoryTextView.text = transaction.category
    }
}
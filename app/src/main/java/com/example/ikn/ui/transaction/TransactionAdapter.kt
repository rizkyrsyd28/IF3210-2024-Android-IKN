package com.example.ikn.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.ikn.R

class TransactionAdapter(
    private val itemClickListener: OnTransactionItemClickListener?,
    private val itemLongClickListener: OnTransactionItemLongClickListener?,
    private val itemDeleteListener: OnDeleteListener?
) :
    ListAdapter<Transaction, TransactionViewHolder>(TransactionDiffCallback) {

    interface OnTransactionItemClickListener {
        fun onItemClick(transactionLocation: String)
    }

    interface OnTransactionItemLongClickListener {
        fun onItemLongClick(transactionId: Int)
    }

    interface OnDeleteListener {
        fun onDeleteItem(transactionId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)

        return TransactionViewHolder(view, itemClickListener, itemLongClickListener)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }
}

object TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.date == newItem.date
    }

}
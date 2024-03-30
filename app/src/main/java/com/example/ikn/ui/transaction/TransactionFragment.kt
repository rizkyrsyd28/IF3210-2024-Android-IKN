package com.example.ikn.ui.transaction

import android.content.Intent
import android.net.Uri
import com.example.ikn.R


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment : Fragment(), TransactionAdapter.OnTransactionItemLongClickListener,
    TransactionAdapter.OnDeleteListener, TransactionAdapter.OnTransactionItemClickListener {

    private val transactionViewModel: TransactionViewModel by viewModels { TransactionViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_transaction, container, false)

        val transactionRecyclerView: RecyclerView = rootView.findViewById(R.id.rvTransaction)
        val transactionAdapter =
            TransactionAdapter(
                itemClickListener = this,
                itemLongClickListener = this,
                itemDeleteListener = this
            )

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = transactionAdapter.currentList[position]
                onDeleteItem(transaction.id)
            }
        })

        itemTouchHelper.attachToRecyclerView(transactionRecyclerView)

        transactionRecyclerView.adapter = transactionAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        transactionRecyclerView.layoutManager = layoutManager

//        // Dummy data
//        val dummyTransactions = listOf(
//            Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
//            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
//            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta"),
//            Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
//            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
//            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta"),
//            Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
//            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
//            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta"),
//            Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
//            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
//            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta")
//        )
//
//        transactionAdapter.submitList(dummyTransactions)

        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter.submitList(transactionList)
        }

        val newTransactionFloatingActionButton =
            rootView.findViewById<FloatingActionButton>(R.id.fabAddTransaction)
        newTransactionFloatingActionButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewTransactionFragment())
                .setReorderingAllowed(true)
                .addToBackStack("new_transaction")
                .commit()
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onItemLongClick(transactionId: Int) {
        lifecycleScope.launch {
            val clickedTransaction = TransactionRepository.getInstance(
                AppDatabase.getInstance(requireContext()).transactionDao()
            ).getTransactionById(transactionId)

            if (clickedTransaction != null) {
                val fragmentInstance = UpdateTransactionFragment.newInstance(
                    clickedTransaction.id,
                    clickedTransaction.name,
                    clickedTransaction.date,
                    clickedTransaction.amount,
                    clickedTransaction.location,
                    clickedTransaction.category
                )

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentInstance)
                    .setReorderingAllowed(true)
                    .addToBackStack("update_transaction")
                    .commit()
            }
        }
    }

    override fun onDeleteItem(transactionId: Int) {
        lifecycleScope.launch {
            TransactionRepository.getInstance(
                AppDatabase.getInstance(requireContext()).transactionDao()
            ).deleteTransaction(transactionId)
        }
    }

    override fun onItemClick(transactionLocation: String) {
        val gmmIntentURI = Uri.parse("geo:0,0?q=" + Uri.encode(transactionLocation))
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentURI)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
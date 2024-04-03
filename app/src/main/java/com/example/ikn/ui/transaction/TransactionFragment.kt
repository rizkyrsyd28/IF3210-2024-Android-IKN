package com.example.ikn.ui.transaction

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import com.example.ikn.R


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionRepository
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.utils.SharedPreferencesManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment() : Fragment(), TransactionAdapter.OnTransactionItemLongClickListener,
    TransactionAdapter.OnDeleteListener, TransactionAdapter.OnTransactionItemClickListener {

    private val transactionViewModel: TransactionViewModel by viewModels { TransactionViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
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

        val tvTransactionNameLatest = rootView.findViewById<TextView>(R.id.tvTransactionNameLatest)
        val tvTransactionAmountLatest = rootView.findViewById<TextView>(R.id.tvAmountLatest)
        val tvTransactionDateLatest = rootView.findViewById<TextView>(R.id.tvDateLatest)
        val tvTransactionLocationLatest = rootView.findViewById<TextView>(R.id.tvLocationLatest)
        val tvTransactionCategoryLatest = rootView.findViewById<TextView>(R.id.tvCategoryLatest)

        val latestTransactionCardView = rootView.findViewById<CardView>(R.id.cvLatestTransaction)

        val tvTotalAmount = rootView.findViewById<TextView>(R.id.tvAmountTotal)

        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter.submitList(transactionList)
            if (transactionList.isNotEmpty()) {
                tvTransactionNameLatest.text = transactionList[0].name
                tvTransactionAmountLatest.text =
                    NumberFormat.getNumberInstance(Locale.US).format(transactionList[0].amount)
                tvTransactionLocationLatest.text = transactionList[0].location
                tvTransactionDateLatest.text = transactionList[0].date
                tvTransactionCategoryLatest.text = transactionList[0].category
            } else {
                latestTransactionCardView.visibility = View.GONE
            }
        }

        transactionViewModel.totalAmount.observe(viewLifecycleOwner) { totalAmount ->
            tvTotalAmount.text = NumberFormat.getNumberInstance(Locale.US).format(totalAmount)
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

        val nim =
            SharedPreferencesManager(requireContext()).get(PreferenceRepository.emailKey).toString()
                .substringBefore("@")

        Log.d("MIGRATION", "Nim: $nim")

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
        fun newInstance() =
            TransactionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemLongClick(transactionId: Int) {
        lifecycleScope.launch {
            val clickedTransaction = TransactionRepository.getInstance(
                AppDatabase.getInstance(requireContext()).transactionDao(),
                SharedPreferencesManager(requireContext())
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
                AppDatabase.getInstance(requireContext()).transactionDao(),
                SharedPreferencesManager(requireContext())
            ).deleteTransaction(transactionId)
        }
    }

    override fun onItemClick(transactionLocation: String) {

        val pattern = "\\((-?\\d+\\.\\d+), (-?\\d+\\.\\d+)\\)".toRegex()

        val matchResult = pattern.find(transactionLocation)

        val coordinates = matchResult?.let { result ->
            val (lat, lon) = result.destructured
            Pair(lat, lon)
        }

        val uriString = if (coordinates == null) {
            "geo:0,0?q=" + Uri.encode(transactionLocation)
        } else {
            val lat = coordinates.first
            val lon = coordinates.second
            "geo:0,0?q=$lat,$lon"
        }

        Log.d("MAP Intent", "URI String: $uriString")

        val gmmIntentURI = Uri.parse(uriString)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentURI)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
}
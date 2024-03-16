package com.example.ikn.ui.transaction

import com.example.ikn.R


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_transaction, container, false)

        val transactionRecyclerView: RecyclerView = rootView.findViewById(R.id.rvTransaction)
        val transactionAdapter = TransactionAdapter()
        transactionRecyclerView.adapter = transactionAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        transactionRecyclerView.layoutManager = layoutManager

        // Dummy data
        val dummyTransactions = listOf(
            Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta"),
                    Transaction("9 Sep 2011", "Pembelian", "Crisbar", 49000, "Cisitu"),
            Transaction("30 Sep 1965", "Pemberontakan", "Lubang Buaya", 70000, "Jakarta"),
            Transaction("17 Aug 1945", "Kemerdekaan", "Proklamasi", 10000, "Jakarta")
        )

        transactionAdapter.submitList(dummyTransactions)

        // Update the UI
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                transactionViewModel.transactionHistory.collect { transactionHistory ->
//                    transactionAdapter.submitList(transactionHistory.history)
//                }
//            }
//        }

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
}
package com.example.ikn.ui.transaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.ikn.R
import com.example.ikn.viewmodels.BottomNavViewModel
import kotlinx.coroutines.flow.observeOn

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionHostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionHostFragment : Fragment() {

    private val bottomNavViewModel: BottomNavViewModel by activityViewModels()

    private val randomizeTransactionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            bottomNavViewModel.setShowNewTransaction(true)
            findNavController().navigate(R.id.nav_transaction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        val filter = IntentFilter("com.example.ikn.RANDOM_TRANSACTION")

        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            ContextCompat.RECEIVER_EXPORTED
        } else {
            ContextCompat.RECEIVER_NOT_EXPORTED
        }

        ContextCompat.registerReceiver(
            requireActivity(),
            randomizeTransactionBroadcastReceiver,
            filter,
            receiverFlags
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavViewModel.addRandomTransaction.observe(viewLifecycleOwner, Observer { addRandomTransaction ->
            if (addRandomTransaction) {
                val randomNewTransactionFragment = NewTransactionFragment.newInstance(true)
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, randomNewTransactionFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack("new_transaction")
                    .commit()
                bottomNavViewModel.setShowNewTransaction(false)
            }
        })

        val defaultFragment = TransactionFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, defaultFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            "TransactionFragmentManager",
            "Back stack entry count: ${childFragmentManager.backStackEntryCount}"
        )
        childFragmentManager.restoreBackStack("new_transaction")
        childFragmentManager.restoreBackStack("update_transaction")
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(randomizeTransactionBroadcastReceiver)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionHostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionHostFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
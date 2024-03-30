package com.example.ikn.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ikn.R
import com.example.ikn.data.AppDatabase
import com.example.ikn.data.TransactionCategory
import com.example.ikn.data.TransactionRepository
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [NewTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewTransactionFragment : Fragment() {

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepository.getInstance(
            AppDatabase.getInstance(requireContext()).transactionDao()
        )
    }

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

        val rootView = inflater.inflate(R.layout.fragment_new_transaction, container, false)

        val textIdleColor = ContextCompat.getColor(requireContext(),
            R.color.md_theme_light_onTertiaryContainer
        )
        val textFocusColor = ContextCompat.getColor(requireContext(),
            R.color.md_theme_light_onPrimary
        )

        val categories = TransactionCategory.entries.toTypedArray()
        val autoCompleteTextView =
            rootView.findViewById<AutoCompleteTextView>(R.id.autoCategoryNewTransaction)
        val adapterItems =
            TransactionCategoryAdapter(requireContext(), R.layout.item_dropdown, categories)
        autoCompleteTextView.setAdapter(adapterItems)

        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.hint = " "
                autoCompleteTextView.showDropDown()
            } else {
                autoCompleteTextView.hint = "Category"
            }
        }


        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedTransactionCategory =
                parent.getItemAtPosition(position) as TransactionCategory
            autoCompleteTextView.setText(selectedTransactionCategory.toString())
            autoCompleteTextView.hint = " "
        }

        val etNameNewTransaction = rootView.findViewById<EditText>(R.id.etNameNewTransaction)

        etNameNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etNameNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etNameNewTransaction.setTextColor(textIdleColor)
                }
            }
        })

        val etAmountNewTransaction = rootView.findViewById<EditText>(R.id.etAmountNewTransaction)

        etAmountNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etAmountNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etAmountNewTransaction.setTextColor(textIdleColor)
                }
            }
        })

        val etLocationNewTransaction = rootView.findViewById<EditText>(R.id.etLocationNewTransaction)

        etLocationNewTransaction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if text is not empty
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    etLocationNewTransaction.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    etLocationNewTransaction.setTextColor(textIdleColor)
                }
            }
        })

        val saveButton = rootView.findViewById<Button>(R.id.btnSaveNewTransaction)
        saveButton.setOnClickListener {

            val nameInput = etNameNewTransaction.text.toString()
            val amountInput = etAmountNewTransaction.text.toString().toInt()
            val locationInput = etLocationNewTransaction.text.toString()

            val newTransaction = Transaction(
                name = nameInput,
                amount = amountInput,
                location = locationInput,
                category = autoCompleteTextView.text.toString()
            )

            lifecycleScope.launch {
                transactionRepository.insertTransaction(newTransaction)
                parentFragmentManager.popBackStack()
            }

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
         * @return A new instance of fragment NewTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewTransactionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
package com.example.ikn.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.ikn.R
import java.util.Calendar

private const val ARG_TRANSACTION_ID = "transaction_id"
private const val ARG_TRANSACTION_NAME = "transaction_name"
private const val ARG_TRANSACTION_DATE = "transaction_date"
private const val ARG_TRANSACTION_AMOUNT = "transaction_amount"
private const val ARG_TRANSACTION_LOCATION = "transaction_location"
private const val ARG_TRANSACTION_CATEGORY = "transaction_category"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateTransactionFragment : Fragment() {
    private var transactionId: Int? = null
    private var transactionName: String? = null
    private var transactionDate: String? = null
    private var transactionAmount: Int? = null
    private var transactionLocation: String? = null
    private var transactionCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(ARG_TRANSACTION_ID)
            transactionName = it.getString(ARG_TRANSACTION_NAME)
            transactionDate = it.getString(ARG_TRANSACTION_DATE)
            transactionAmount = it.getInt(ARG_TRANSACTION_AMOUNT)
            transactionLocation = it.getString(ARG_TRANSACTION_LOCATION)
            transactionCategory = it.getString(ARG_TRANSACTION_CATEGORY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_update_transaction, container, false)

        val textIdleColor = ContextCompat.getColor(requireContext(),
            R.color.md_theme_light_onTertiaryContainer
        )
        val textFocusColor = ContextCompat.getColor(requireContext(),
            R.color.md_theme_light_onPrimary
        )

        // Set location and category to be un-clickable
        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(R.id.autoCategoryNewTransaction)
        autoCompleteTextView.isEnabled = false
        autoCompleteTextView.isClickable = false
        autoCompleteTextView.isFocusable = false

        val locationEditText = rootView.findViewById<EditText>(R.id.etLocationNewTransaction)
        locationEditText.isEnabled = false
        locationEditText.isClickable = false
        locationEditText.isFocusable = false

        val dateMaxLength = 10
        val date = rootView.findViewById<EditText>(R.id.etDateNewTransaction)
        val dateFilters = arrayOf<InputFilter>(InputFilter.LengthFilter(dateMaxLength))
        date.filters = dateFilters

        date.addTextChangedListener(object : TextWatcher {

            private var currentLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                not used
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    // Change text color dynamically
                    date.setTextColor(textFocusColor)
                } else {
                    // Set default text color when no text is entered
                    date.setTextColor(textIdleColor)
                }

                val text = s.toString()
                val length = text.length

                if (length == 2 && currentLength < length) {
                    date.setText("$text/")
                    date.setSelection(date.text.length)
                } else if (length == 5 && currentLength < length) {
                    date.setText("$text/")
                    date.setSelection(date.text.length)
                } else if (length < currentLength) {
                    // Handle deletion
                    if (text.endsWith("/")) {
                        // Remove the "/" if the user deletes it
                        date.setText(text.substring(0, text.length - 1))
                        date.setSelection(date.text.length)
                    }
                }

                currentLength = length

            }

        })

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param transactionId Parameter 1.
         * @param transactionName Parameter 1.
         * @param transactionDate Parameter 1.
         * @param transactionAmount Parameter 1.
         * @param transactionLocation Parameter 1.
         * @param transactionCategory Parameter 1.
         * @return A new instance of fragment UpdateTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            transactionId: Int,
            transactionName: String,
            transactionDate: String,
            transactionAmount: Int,
            transactionLocation: String,
            transactionCategory: String
        ) = UpdateTransactionFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TRANSACTION_ID, transactionId)
                putString(ARG_TRANSACTION_NAME, transactionName)
                putString(ARG_TRANSACTION_DATE, transactionDate)
                putInt(ARG_TRANSACTION_AMOUNT, transactionAmount)
                putString(ARG_TRANSACTION_LOCATION, transactionLocation)
                putString(ARG_TRANSACTION_CATEGORY, transactionCategory)
            }
        }
    }
}
package com.example.ikn.ui.Settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ikn.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSettingsSavedTransactions.setOnClickListener {
            askExtension();
        }

    }

    fun askExtension() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val message = "Which excel extension do you wanted to save?"
        val title = "Excel Extension"
        val posBtn = ".xlsx"
        val negBtn = ".xls"

        builder
            .setMessage(message.toString())
            .setTitle(title.toString())
            .setPositiveButton(posBtn.toString()) { dialog, which ->
                // Do something.
            }
            .setNegativeButton(negBtn.toString()) { dialog, which ->
                // Do something else.
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
package com.example.ikn.ui.Settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ikn.databinding.FragmentSettingsBinding
import com.example.ikn.ui.transaction.Transaction
import java.io.File


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }
    private var transactionList : List<Transaction>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            this.transactionList = transactionList
        }

        binding.btnSettingsSavedTransactions.setOnClickListener {
            saveTransactions();
        }
        binding.btnLogout.setOnClickListener {
            settingsViewModel.signOutHandle()
            startActivity(Intent(activity, SplashActivity::class.java))
            activity?.finish()
        }

    }

    private fun saveTransactions() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val message = "Which excel extension do you wanted to save?"
        val title = "Excel Extension"
        val posBtn = ".xlsx"
        val negBtn = ".xls"

        builder
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(posBtn) { dialog, which ->
                binding.progressBarCyclic.visibility = View.VISIBLE
                dialog.dismiss()
                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, true)
                openExcel(filepath)
                binding.progressBarCyclic.visibility = View.GONE
            }
            .setNegativeButton(negBtn) { dialog, which ->
                binding.progressBarCyclic.visibility = View.VISIBLE
                dialog.dismiss()
                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, false)
                openExcel(filepath)
                binding.progressBarCyclic.visibility = View.GONE
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun openExcel(filePath: String) {
        Log.e("SettingsFragment", filePath)
        val file: File = File(filePath)
        val fileUri = FileProvider.getUriForFile(requireContext(), "com.example.ikn.fileprovider", file);

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            fileUri,
            "application/vnd.ms-excel"
        )

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

}

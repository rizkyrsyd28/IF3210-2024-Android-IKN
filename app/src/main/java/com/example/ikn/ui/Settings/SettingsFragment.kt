package com.example.ikn.ui.Settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ikn.SplashActivity
import com.example.ikn.databinding.FragmentSettingsBinding
import com.example.ikn.service.file.FileService
import com.example.ikn.service.file.FileBroadcastReceiver
import com.example.ikn.ui.transaction.Transaction
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var fileReceiver: FileBroadcastReceiver
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }
    private var transactionList : List<Transaction>? = null

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fileReceiver = FileBroadcastReceiver(
            openHandler = { file -> openExcel(file) },
            sendEmailHandler = {file, address -> sendEmail(file, address)}
        )
        val filter = IntentFilter().apply {
            addAction("com.example.ikn.OPEN_FILE")
            addAction("com.example.ikn.OPEN_EMAIL")

        }
        requireContext().registerReceiver(fileReceiver, filter)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!requireActivity().intent.extras?.getBoolean("status")!!) {
            binding.btnSettingsRandomTransactions.visibility = View.GONE
            binding.btnSettingsSendTransactions.visibility = View.GONE
        }

        settingsViewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            this.transactionList = transactionList
        }

        binding.btnSettingsRandomTransactions.setOnClickListener {
            Intent().also { intent ->
                intent.setAction("com.example.ikn.RANDOM_TRANSACTION")
                requireActivity().sendBroadcast(intent)
            }
        }

        binding.btnSettingsSavedTransactions.setOnClickListener {
            saveTransactions()
        }

        binding.btnSettingsSendTransactions.setOnClickListener {
            sendTransactions()
        }

        binding.btnLogout.setOnClickListener {
            if (requireActivity().intent.extras?.getBoolean("status")!!) settingsViewModel.signOutHandle()
            Log.e(TAG, "Status - ${requireActivity().intent.extras?.getBoolean("status")!!}")
            startActivity(Intent(activity, SplashActivity::class.java))
            activity?.finish()
        }

    }
    private fun fileIntent(isXlsx: Boolean, isSend: Boolean) {
        val action =
            if (isSend) "com.example.ikn.SEND_FILE"
            else "com.example.ikn.CREATE_FILE"

        val intent = Intent(requireContext(), FileService::class.java)
            .setAction(action)
            .putExtra("DIR", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path)
            .putExtra("XLSX", isXlsx)
        requireContext().startService(intent)
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
//                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, true)
//                openExcel(filepath)
                fileIntent(true, false)
                binding.progressBarCyclic.visibility = View.GONE
                dialog.dismiss()
            }
            .setNegativeButton(negBtn) { dialog, which ->
                binding.progressBarCyclic.visibility = View.VISIBLE
                dialog.dismiss()
//                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, false)
//                openExcel(filepath)
                fileIntent(false, false)
                binding.progressBarCyclic.visibility = View.GONE
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun sendTransactions() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val message = "Which excel extension do you wanted to send via email?"
        val title = "Excel Extension"
        val posBtn = ".xlsx"
        val negBtn = ".xls"

        builder
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton(posBtn) { dialog, which ->
                binding.progressBarCyclic.visibility = View.VISIBLE
                dialog.dismiss()
//                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, true)
//                openExcel(filepath)
                fileIntent(true, true)
                binding.progressBarCyclic.visibility = View.GONE
                dialog.dismiss()
            }
            .setNegativeButton(negBtn) { dialog, which ->
                binding.progressBarCyclic.visibility = View.VISIBLE
                dialog.dismiss()
//                val filepath = settingsViewModel.createExcel(this.transactionList, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path, false)
//                openExcel(filepath)
                fileIntent(false, true)
                binding.progressBarCyclic.visibility = View.GONE
                dialog.dismiss()
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
        requireContext().startActivity(intent)
    }
    private fun sendEmail(filePath: String, address: String) {
        val file: File = File(filePath)
        val fileUri = FileProvider.getUriForFile(requireContext(), "com.example.ikn.fileprovider", file);

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd-MM-yyyy")
        val formattedDateTime = currentDateTime.format(formatter)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, "List Transaction per $formattedDateTime")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_TEXT, "Berikut terlampir daftar transaction")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
//        requireContext().startActivity(intent)
        val chooser = Intent.createChooser(intent, "Send Email via")
        requireContext().startActivity(chooser)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(fileReceiver)
    }
    companion object {
        const val TAG = "[SETTING FRAGMENT]"
    }
}

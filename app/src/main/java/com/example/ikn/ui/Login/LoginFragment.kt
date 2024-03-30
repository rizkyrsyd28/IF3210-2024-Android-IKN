package com.example.ikn.ui.Login

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.ikn.MainActivity
import com.example.ikn.databinding.FragmentLoginBinding
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var receiver : BroadcastReceiver
    private lateinit var signInBtn: Button

    private val emailRegex = Regex("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        signInBtn = binding.signInButton

        val repo = Repository()
        val sharedPref = SharedPreferencesManager(requireContext())
        val viewModel = LoginViewModel(repo, sharedPref)

        signInBtn.setOnClickListener {
            Log.i("[LOGIN LISTENER]", "In Login Listener")
            signInHandler(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                viewModel
                )
        }

        viewModel.authorized.observe(viewLifecycleOwner, Observer { token ->
            if (token) {
                   Handler(Looper.getMainLooper()).postDelayed(
                       {
                           startActivity(Intent(activity, MainActivity::class.java))
                           activity?.finish()
                       }
                   , 3000)
            }
        })

        return binding.root
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != "NETWORK_STATUS" && intent?.extras?.isEmpty!!) return
//                Log.i("[RECEIVE BROADCAST]", "Hasil ${intent.action} , ${intent.extras?.getBoolean("status")}")
                val status = intent.extras?.getBoolean("status")!!

                signInBtn.isEnabled = status

                if (status) {
//                    CoroutineScope(Dispatchers.Main).launch {
                        showToast("Internet Connected")
//                    }
                } else {
//                    CoroutineScope(Dispatchers.Main).launch {
                        showToast("Internet Disconnected")
//                    }
                }
            }
        }
        context?.registerReceiver(receiver, IntentFilter("NETWORK_STATUS"))
    }
    private fun validateSignIn(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) return false
        return emailRegex.matches(email)
    }
    private fun signInHandler(email: String, password: String, viewModel: LoginViewModel) {
        if (!validateSignIn(email, password)) {
//            showToast()
            Toast.makeText(requireActivity(), "Email or Password Invalid", Toast.LENGTH_SHORT, ).show()
            return
        }
        viewModel.loginHandler(email, password)
    }
    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT, ).show()
        }
    }
    companion object {
        fun newInstance() = LoginFragment()
    }

}
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
import com.example.ikn.repository.PreferenceRepository
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

        val sharedPref = SharedPreferencesManager(requireContext())
        val repo = Repository()
        val prefRepo = PreferenceRepository(sharedPref)
        val viewModel = LoginViewModel(repo, prefRepo)

        /* Check isi Shared Pref */
        Log.i("[LOGIN]", "isKeep ${prefRepo.isKeepLoggedIn()}, email ${prefRepo.getSignInInfo().first} ${prefRepo.getSignInInfo().first}")

        if (viewModel.isAllowKeepLoggedIn()) {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        signInBtn.setOnClickListener {
            Log.i("[LOGIN LISTENER]", "In Login Listener")
            signInHandler(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                isKeepLoggedIn = binding.checkBox.isChecked,
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
                   , 1000)
            }
        })

        viewModel.failed.observe(viewLifecycleOwner, Observer { error ->
            if (error) {
                showToast(viewModel.failedMessage)
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

                val status = intent.extras?.getBoolean("status")!!

                signInBtn.isEnabled = status

                if (status) showToast("Internet Connected") else showToast("Internet Disconnected")
            }
        }
        context?.registerReceiver(receiver, IntentFilter("NETWORK_STATUS"))
    }
    private fun validateSignIn(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) return false
        return emailRegex.matches(email)
    }
    private fun signInHandler(email: String, password: String, isKeepLoggedIn: Boolean, viewModel: LoginViewModel) {
        try {
            if (!validateSignIn(email, password)) {
//            Toast.makeText(requireActivity(), "Email or Password Invalid", Toast.LENGTH_SHORT, ).show()
                showToast("Email or Password Invalid")
                return
            }
            viewModel.signInHandler(email, password, isKeepLoggedIn)
        } catch (exp: Exception) {
            showToast("Failed to Sign In")
        }
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
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
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.ikn.MainActivity
import com.example.ikn.databinding.FragmentLoginBinding
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.repository.Repository
import com.example.ikn.service.token.TokenService
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment: Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var receiver : BroadcastReceiver
    private lateinit var signInBtn: Button
    private lateinit var asGuestBtn: Button

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    private val emailRegex = Regex("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        signInBtn = binding.signInButton
        asGuestBtn = binding.asGuestButton

        if (loginViewModel.isAllowKeepLoggedIn()) {
            loginViewModel.signAsGuestHandler(email = binding.emailEditText.text.toString())
            startActivity(Intent(activity, MainActivity::class.java).apply {
                putExtra("status", true)
            })
            activity?.finish()
        }

        signInBtn.setOnClickListener {
            Log.i("[LOGIN LISTENER]", "In Login Listener")
            signInHandler(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                isKeepLoggedIn = binding.checkBox.isChecked,
                loginViewModel
                )
        }

        asGuestBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java).apply {
                putExtra("status", false)
            })
            requireActivity().finish()
        }

        loginViewModel.authorized.observe(viewLifecycleOwner, Observer { token ->
            if (token) {
                   Handler(Looper.getMainLooper()).postDelayed(
                       {
                           startActivity(Intent(activity, MainActivity::class.java).apply {
                               putExtra("status", true)
                           })
                           activity?.finish()
                       }
                   , 1000)
            }
        })

        loginViewModel.failed.observe(viewLifecycleOwner, Observer { error ->
            if (error) {
                showToast(loginViewModel.failedMessage)
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
                signInBtn.visibility = if (status) View.VISIBLE else View.GONE
                asGuestBtn.isEnabled = !status
                asGuestBtn.visibility = if (!status) View.VISIBLE else View.GONE

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
                showToast("Email or Password Invalid")
                return
            }
            loginViewModel.signInHandler(email, password, isKeepLoggedIn)
        } catch (exp: Exception) {
            showToast("Failed to Sign In")
        }
    }
    private fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT, ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Destroy Login Fragment")
    }
    companion object {
        fun newInstance() = LoginFragment()
        const val TAG = "[LOGIN FRAGMENT]"
    }

}
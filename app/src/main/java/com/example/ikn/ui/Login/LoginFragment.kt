package com.example.ikn.ui.Login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.ikn.MainActivity
import com.example.ikn.R
import com.example.ikn.databinding.FragmentLoginBinding
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager

class LoginFragment: Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val repo = Repository()
        val sharedPref = SharedPreferencesManager(requireContext())
        val viewModel = LoginViewModel(repo, sharedPref)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            Log.i("[LOGIN]" , "(email : $email, password : $password)")

            viewModel.loginHandler(email, password)
        }

        viewModel.authorized.observe(viewLifecycleOwner, Observer { token ->
            if (token) {
                   Handler(Looper.getMainLooper()).postDelayed(
                       {
                           startActivity(Intent(activity, MainActivity::class.java))
                       }
                   , 3000)
            }
        })

        return binding.root
    }

    companion object {
        fun newInstance() = LoginFragment()
    }

}
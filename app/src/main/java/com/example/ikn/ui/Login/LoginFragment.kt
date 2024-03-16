package com.example.ikn.ui.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ikn.R
import com.example.ikn.databinding.FragmentLoginBinding
import com.example.ikn.repository.Repository

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val repo = Repository()
        val viewModel = LoginViewModel(repo)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            Log.i("[LOGIN]" , "(email : $email, password : $password)")

            viewModel.loginHandler(email, password)
        }

        return binding.root
    }

    companion object {
        fun newInstance() = LoginFragment()
    }

}
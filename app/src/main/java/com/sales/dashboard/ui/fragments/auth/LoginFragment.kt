package com.sales.dashboard.ui.fragments.auth

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sales.dashboard.R
import java.util.concurrent.TimeUnit
import com.sales.dashboard.databinding.FragmentLoginBinding

class LoginFragment(
    val sendPhoneNumber: (number: String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(LayoutInflater.from(context), container, false)

        binding.cvNext.setOnClickListener {
            validateText(binding.etPhoneNumber.text.toString())
        }

        return binding.root
    }

    private fun validateText(phoneNumber: String){
        if (phoneNumber.length == 10){
            val string = "+${91}${phoneNumber}"
            sendPhoneNumber(string)
            dismiss()
        }else{
            Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

}
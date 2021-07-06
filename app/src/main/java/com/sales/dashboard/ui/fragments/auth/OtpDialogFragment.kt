package com.sales.dashboard.ui.fragments.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.sales.dashboard.R
import com.sales.dashboard.database.Profile
import com.sales.dashboard.databinding.FragmentOtpDialogListDialogBinding
import com.sales.dashboard.viewmodel.HomeViewModel
import java.util.concurrent.TimeUnit

class OtpDialogFragment(
    private val number: String,
    private val activity: Activity,
    val changeNumber: () -> Unit,
    val adminCall: (profile: Profile) -> Unit,

) : BottomSheetDialogFragment() {

    private var _binding: FragmentOtpDialogListDialogBinding? = null

    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var storedVerificationId: String = ""
    private lateinit var options: PhoneAuthOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpDialogListDialogBinding.inflate(inflater, container, false)
        verify()

        binding.cvNext.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, binding.otpView.otp!!)
            signInWithPhoneAuthCredential(credential)
        }

        binding.txtContent.text = "An SMS containing the otp will be received to the registered phone number $number"

        binding.txtWrongNumber.setOnClickListener {
            goBackToLogin()
        }

        return binding.root
    }

    private fun verify() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    toast("Invalid request!")
                } else if (e is FirebaseTooManyRequestsException) {
                    toast("The SMS quota for the project has been exceeded")
                }

                goBackToLogin()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
//                resendToken = token
            }
        }

        options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun goBackToLogin() {
        dismiss()
        changeNumber()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    setUpDataBase()

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        toast("${task.exception}")
                    }
                }
            }
    }

    private fun setUpDataBase() {
        db.collection("Users")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    var exits = false

                    for (documents in it.result!!){
                        if (documents.get("phonenumber") == number){
                            val profile = Profile(phoneNumber = 565, password = "hello", profileImage = "", admin = documents.get("admin") as Boolean)
                            adminCall(profile)
                            exits = true
                            break
                        }
                    }

                    val profile = Profile(phoneNumber = 565, password = "hello", profileImage = "", admin = false)

                    if (!exits){
                        addToDb(profile)
                    }else{
                        dismiss()
                    }

                }else {
                    Log.w(TAG, "Error getting documents.", it.exception);
                }
            }
    }

    private fun addToDb(profile : Profile) {
        val user = HashMap<String, Any>()
        user["admin"] = false
        user["password"] = "random"
        user["phonenumber"] = number
        user["userName"] = "HelloWorld!"


        db.collection("Users")
            .add(user)
            .addOnSuccessListener {
                adminCall(profile)
                dismiss()
            }.addOnFailureListener{
                toast("Try again!")
                goBackToLogin()
            }
    }

    private fun toast(text: String){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
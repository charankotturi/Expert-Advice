package com.sales.dashboard.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.sales.dashboard.RetrofitInstance
import com.sales.dashboard.databinding.DialogAddContentBinding
import com.sales.dashboard.model.NotificationData
import com.sales.dashboard.model.Post
import com.sales.dashboard.model.PushNotification
import com.sales.dashboard.ui.PassData
import com.sales.dashboard.ui.fragments.auth.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

const val TOPIC = "/topics/myTopic2"

class DialogContentAdd(
    private val sendData: () -> Unit
) : BottomSheetDialogFragment(){

    private lateinit var  binding: DialogAddContentBinding
    private val database = FirebaseFirestore.getInstance()

    private val TAG = "DialogContentAdd"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddContentBinding.inflate(LayoutInflater.from(context), container, false)

        binding.cvNext.setOnClickListener {
            if (binding.etHeader.text?.length != 0){
                if (binding.etContent.text?.length != 0){

                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                    val currentDate = sdf.format(Date())

                    val post = Post(binding.etHeader.text.toString()
                    ,binding.etContent.text.toString(), currentDate)

                    Log.i("TAG", "onCreateView: $post")

                    val hashMap = HashMap<String, String>()
                    hashMap["content"] = binding.etContent.text.toString()
                    hashMap["header"] = binding.etHeader.text.toString()
                    hashMap["date"] = currentDate

                    database.collection("Posts")
                        .add(hashMap)
                        .addOnSuccessListener {

                            PushNotification(
                                NotificationData(binding.etHeader.text.toString(), binding.etContent.text.toString()),
                                TOPIC
                            ).also {
                                sendNotification(it)
                            }

                        }.addOnFailureListener{
                            Log.i("TAG", "onCreateView: Failed")
                            dismiss()
                        }
                }
            }else{

                Toast.makeText(context,
                    "Enter something in header and content!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        return binding.root
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                sendData()

                dismiss()
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }


}
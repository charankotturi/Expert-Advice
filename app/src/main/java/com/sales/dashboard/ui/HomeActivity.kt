package com.sales.dashboard.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.sales.dashboard.R
import com.sales.dashboard.databinding.ActivityMainBinding
import com.sales.dashboard.ui.fragments.auth.LoginFragment
import com.sales.dashboard.ui.fragments.auth.OtpDialogFragment
import com.sales.dashboard.ui.fragments.home.DialogContentAdd
import com.sales.dashboard.ui.fragments.home.TOPIC
import com.sales.dashboard.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: HomeViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()

    private lateinit var passData: PassData
    private var contentPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUp()

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        viewModel.allProfiles.observe(this){
            if (it.isNotEmpty()){
                val profile = it[0]
                if (!profile.admin){
                    binding.fabAdd.visibility = View.GONE
                }else{
                    binding.fabAdd.visibility = View.VISIBLE
                }
            }
        }

        binding.txtDashboard.setOnClickListener {

            if (contentPage){
                binding.frameSelector.animate().translationX(binding.txtDashboard.width.toFloat()).start()
                contentPage = !contentPage

                findNavController(R.id.fragmentContainerView2).navigate(R.id.stockFragment)
                binding.collapseToolBar.title = "Dashboard"
            }

        }

        binding.txtContent.setOnClickListener {

            if (!contentPage){
                binding.frameSelector.animate().translationXBy(-binding.txtDashboard.width.toFloat()).start()
                contentPage = !contentPage

                findNavController(R.id.fragmentContainerView2).navigate(R.id.contentFragment)
                binding.collapseToolBar.title = "Content"
            }
        }

    }

    private fun setUp() {
        val user = auth.currentUser

        if (user != null){
            setUpMain()
        }else{
            setUpAuth()
        }
    }

    @SuppressLint("ResourceType")
    private fun setUpMain() {

        binding.imgLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.fabAdd.setOnClickListener {
            val dialog = DialogContentAdd(){
                findNavController(R.id.fragmentContainerView2).navigate(R.id.contentFragment)
            }

            dialog.show(this.supportFragmentManager, "Content Add Dialog")
        }
    }

    private fun setUpAuth() {
        val dialog = LoginFragment {
            val otpDialog = OtpDialogFragment(it, this,{
                setUpAuth()
            },{admin ->
                viewModel.inset(admin)
                if (!admin.admin){
                    binding.fabAdd.visibility = View.GONE
                }else{
                    binding.fabAdd.visibility = View.VISIBLE
                }
            })

            otpDialog.isCancelable = false
            otpDialog.show(this.supportFragmentManager, "OTP-PAGE")
        }

        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "LOGIN-PHONE")
    }
}
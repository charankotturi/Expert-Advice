package com.sales.dashboard.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sales.dashboard.R
import com.sales.dashboard.adapter.ContentAdapter
import com.sales.dashboard.databinding.FragmentContentBinding
import com.sales.dashboard.model.Post
import com.sales.dashboard.ui.PassData
import kotlin.io.path.createTempDirectory


class ContentFragment : Fragment(), PassData {

    private val database = FirebaseFirestore.getInstance()
    private lateinit var binding : FragmentContentBinding
    private var posts = ArrayList<Post>()
    private val adapter1: ContentAdapter = ContentAdapter()

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentBinding.inflate(LayoutInflater.from(context), container, false)

        val user = auth.currentUser

        if (user != null){
            getData()
        }

        adapter1.diff.submitList(posts)

        binding.rvContent.apply {
            adapter = adapter1
            layoutManager= LinearLayoutManager(context)
        }

        return binding.root
    }

    private fun getData(){
        val ref = database.collection("Posts")

        ref.orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener{task ->

                val list = ArrayList<Post>()

                for (children in task.result!!){
                    val post = Post(
                        children.get("header") as String,
                        children.get("content") as String,
                        children.get("date") as String,
                    )
                    Log.i("TAG", "onDataChange: $children")
                    list.add(post)
                }

                val posts = list.sortedWith(compareBy { it.date }).asReversed()

                adapter1.diff.submitList(posts)
                adapter1.notifyDataSetChanged()
                Log.i("TAG", "getData: $posts")

            }
    }

    override fun passData() {
        getData()
    }

}
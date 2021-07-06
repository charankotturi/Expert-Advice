package com.sales.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sales.dashboard.databinding.ContentModelBinding
import com.sales.dashboard.model.Post

class ContentAdapter : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ContentModelBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            val post = diff.currentList[position]
            binding.txtAuther.text = post.header
            binding.txtMessage.text = post.content
            binding.txtDate.text = post.date

            if (position == diff.currentList.size -  1){
                val params = binding.parent.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = 400

            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

    }

    val diff = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ContentModelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)= holder.bind(position)

    override fun getItemCount(): Int = diff.currentList.size
}
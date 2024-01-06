package com.example.communityapp.ui.jobs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.R
import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.ui.feed.FeedsAdapter

class CommentsAdapter (private val commentItem: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.commentNameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentValueTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.commentTimeTextView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentViewHolder {
        val itemView = View.inflate(parent.context, R.layout.comment_item, null)
        return CommentViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = commentItem[position]
        holder.nameTextView.text = currentItem.name
        holder.commentTextView.text = currentItem.value
        holder.dateTextView.text = currentItem.time
    }

    override fun getItemCount(): Int {
        return commentItem.size
    }

}
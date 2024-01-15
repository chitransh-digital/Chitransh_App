package com.example.communityapp.ui.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.models.NewsFeed

class FeedsAdapter(private val newsItems: List<NewsFeed>,private val context: Context) :
    RecyclerView.Adapter<FeedsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headlineTextView: TextView = itemView.findViewById(R.id.headline)
        val descriptionTextView: TextView = itemView.findViewById(R.id.desc)
        val dateTextView: TextView = itemView.findViewById(R.id.timestamp)
        val imageView:ImageView = itemView.findViewById(R.id.imageView)
        val authorTextView:TextView = itemView.findViewById(R.id.author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.feed_container, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsItems[position]
        holder.headlineTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.body
        holder.dateTextView.text = currentItem.timestamp
        holder.authorTextView.text = currentItem.author

        Glide.with(context).load(currentItem.images[0]).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }
}

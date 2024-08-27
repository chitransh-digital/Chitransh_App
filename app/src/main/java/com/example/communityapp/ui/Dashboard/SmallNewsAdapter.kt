package com.example.communityapp.ui.Dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.models.NewsFeed

interface OnItemClickListener {
    fun onItemClick(position: Int, newsList: List<NewsFeed>)
}
class SmallNewsAdapter(private val context: Context, private val newsList: List<NewsFeed>, private val onItemClick: (position: Int, newsList: List<NewsFeed>) -> Unit) :
    RecyclerView.Adapter<SmallNewsAdapter.NewsViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.news_small_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]

        if(currentItem.images.isNotEmpty()){
            Glide.with(context).load(currentItem.images[0]).into(holder.imageView)
        }
        holder.titleTextView.text = currentItem.title
        holder.bodyTextView.text = currentItem.body

        holder.itemView.setOnClickListener {
            onItemClick.invoke(position, newsList)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class NewsViewHolder(itemView: View) : RecyclerView.
    ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_small_news)
        val titleTextView: TextView = itemView.findViewById(R.id.iv_small_news_title)
        val bodyTextView: TextView = itemView.findViewById(R.id.iv_small_body)
    }
}
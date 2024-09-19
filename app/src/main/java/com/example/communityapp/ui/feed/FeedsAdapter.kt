package com.example.communityapp.ui.feed

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.models.NewsFeed
import java.util.Date
import java.util.Locale

class FeedsAdapter(private val newsItems: List<NewsFeed>, private val context: Context) :
    RecyclerView.Adapter<FeedsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headlineTextView: TextView = itemView.findViewById(R.id.headline)
        val descriptionTextView: TextView = itemView.findViewById(R.id.desc)
        val dateTextView: TextView = itemView.findViewById(R.id.timestamp)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val authorTextView: TextView = itemView.findViewById(R.id.author)
        val scrollView: ScrollView = itemView.findViewById(R.id.desc_scroll_view)  // Reference to the ScrollView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.feed_container, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsItems[position]
        holder.headlineTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.body
        holder.dateTextView.text = convertTimestamp(currentItem.timestamp)
        holder.authorTextView.text = currentItem.author

        // Load image
        if (currentItem.images.isNotEmpty()) {
            Glide.with(context).load(currentItem.images[0]).into(holder.imageView)
        }

        // Reset the ScrollView to the top after it has been laid out
        holder.scrollView.post {
            holder.scrollView.scrollTo(0, 0)
        }
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    private fun convertTimestamp(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val date: Date? = inputFormat.parse(timestamp)
        return outputFormat.format(date ?: Date())
    }
}

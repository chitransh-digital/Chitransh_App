package com.example.communityapp.ui.Business

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R

class ImageAdapter(private val images: MutableList<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageBusinessView)
        val cancelButton: ImageView = itemView.findViewById(R.id.imageCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        Glide.with(holder.itemView).load(imageUri).into(holder.imageView)

        holder.cancelButton.setOnClickListener {
            // Remove the image at the clicked position
            images.removeAt(position)
            // Notify adapter about data changes
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}


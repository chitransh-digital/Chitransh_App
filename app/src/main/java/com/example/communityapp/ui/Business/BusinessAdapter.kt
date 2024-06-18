package com.example.communityapp.ui.Business

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.newModels.Business

class BusinessAdapter (private val context : Context, private val Businesses: List<Business>) : RecyclerView.Adapter<BusinessAdapter.BuisnessViewHolder>() {

        inner class BuisnessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val businessName: TextView = itemView.findViewById(R.id.businessname)
            val businessDescription: TextView = itemView.findViewById(R.id.businessDescription)
            val businessLocation: TextView = itemView.findViewById(R.id.tv_businessAdd)
            val businessType: TextView = itemView.findViewById(R.id.tv_businessType)
            val businessImage: ImageView = itemView.findViewById(R.id.iv_businessLogo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuisnessViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.business_item_layout, parent, false)
            return BuisnessViewHolder(view)
        }

        override fun onBindViewHolder(holder: BuisnessViewHolder, position: Int) {
            val address = Businesses[position].landmark + ", " + Businesses[position].city + ", " + Businesses[position].state
            val business = Businesses[position]
            holder.businessName.text = business.name
            holder.businessDescription.text = business.desc
            holder.businessLocation.text = address
            holder.businessType.text=business.type
           if( business.images.isNotEmpty() ) Glide.with(holder.itemView).load(business.images[0]).into(holder.businessImage)


            holder.itemView.setOnClickListener {
                // Create an Intent to open BusinessDetailsActivity
                val intent = Intent(context, BusinessDetailsActivity::class.java).apply {
                    // Pass the selected business data to the intent
                    putExtra("business", business)
                }
                // Start the activity
                context.startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return Businesses.size
        }
}
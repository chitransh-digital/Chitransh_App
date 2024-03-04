package com.example.communityapp.ui.Business

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.R
import com.example.communityapp.data.models.Business

class BusinessAdapter (private val context : Context, private val Businesses: List<Business>) : RecyclerView.Adapter<BusinessAdapter.BuisnessViewHolder>() {

        inner class BuisnessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val businessName: TextView = itemView.findViewById(R.id.businessname)
            val businessDescription: TextView = itemView.findViewById(R.id.businessDescription)
            val businessLocation: TextView = itemView.findViewById(R.id.businessaddress)
            val businessContact : ImageButton = itemView.findViewById(R.id.call_now)
//            val businessImage: ImageView = itemView.findViewById(R.id.businessImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuisnessViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.business_item_layout, parent, false)
            return BuisnessViewHolder(view)
        }

        override fun onBindViewHolder(holder: BuisnessViewHolder, position: Int) {
            val business = Businesses[position]
            holder.businessName.text = business.name
            holder.businessDescription.text = business.desc
            holder.businessLocation.text = business.address
            holder.businessContact.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${business.contact}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                ContextCompat.startActivity(context, intent, null)
            }
//            Glide.with(holder.itemView).load(business.images[0]).into(holder.businessImage)
        }

        override fun getItemCount(): Int {
            return Businesses.size
        }
}
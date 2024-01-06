package com.example.communityapp.ui.Dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ProfileItemLayoutBinding

class profileAdapter(private val members : List<Member>) : RecyclerView.Adapter<profileAdapter.ViewHolder>() {

    class ViewHolder(binding: ProfileItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        var name = binding.profileName
        val contact = binding.profileContact
        val edit = binding.profileEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProfileItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = members[position]

        holder.name.text = model.name
        holder.contact.text = model.contact

        holder.edit.visibility = View.GONE
    }

}
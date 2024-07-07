package com.example.communityapp.ui.karyakarni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.data.newModels.KaryaMember
import com.example.communityapp.databinding.ChildItemBinding

class KaryakarniMemberAdapter (private val memberList : List<KaryaMember>) : RecyclerView.Adapter<KaryakarniMemberAdapter.KaryakarniMemberViewHolder>() {
    class KaryakarniMemberViewHolder(val binding : ChildItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.navUserImage
        val name = binding.memberName
        val position = binding.memberPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryakarniMemberViewHolder {
        return KaryakarniMemberViewHolder(
            ChildItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: KaryakarniMemberViewHolder, position: Int) {
        val model = memberList[position]

        holder.name.text = model.name
        var desig  = ""
        for (i in model.designations) {
            if (desig != "") {
                desig += ", "
            }
            desig += i
        }

        holder.position.text = desig

        Glide.with(holder.itemView.context)
            .load(model.profilePic)
            .centerCrop()
            .into(holder.image)
    }
}
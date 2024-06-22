package com.example.communityapp.ui.karyakarni

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.data.newModels.KaryaMember
import com.example.communityapp.data.newModels.Karyakarni
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.databinding.KaryakarniItemBinding

class KaryaKarniAdapter (private val karyakarniList: List<Karyakarni>, private val OnItemClick : onClickListener) : RecyclerView.Adapter<KaryaKarniAdapter.KaryakarniViewHolder>() {
    class KaryakarniViewHolder(val binding: KaryakarniItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val memberImage = binding.navUserImage
        val KaName = binding.karyakarniName
        val KaLevel = binding.karyakarniLevel
        val KaAdd = binding.karyakarniAddress
        val karyaListView = binding.KaryaListView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryakarniViewHolder {
        return KaryakarniViewHolder(
            KaryakarniItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return karyakarniList.size
    }

    override fun onBindViewHolder(holder: KaryakarniViewHolder, position: Int) {
        val model = karyakarniList[position]
        holder.KaName.text = model.name
        holder.KaLevel.text = model.level
        holder.KaAdd.text = model.address

        Glide.with(holder.itemView.context)
            .load(model.logo)
            .centerCrop()
            .into(holder.memberImage)

        holder.itemView.setOnClickListener {
            OnItemClick.onClick(model.members)
        }
    }

    interface onClickListener {
        fun onClick(member: List<KaryaMember>)

    }
}
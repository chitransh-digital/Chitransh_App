package com.example.communityapp.ui.karyakarni

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.data.newModels.Karyakarni
import com.example.communityapp.databinding.KaryaRecyclerItemBinding

class KaryaKarniAdapter(private val karyakarniList: List<Karyakarni>) : RecyclerView.Adapter<KaryaKarniAdapter.KaryakarniViewHolder>() {
    class KaryakarniViewHolder(val binding: KaryaRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val KaName = binding.karyakarniName
        val KaLevel = binding.karyakarniLevel
        val KaAdd = binding.karyakarniAddress
        val memberRecycler = binding.karyaItemChild
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaryakarniViewHolder {
        return KaryakarniViewHolder(
            KaryaRecyclerItemBinding.inflate(
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

        // Prepare adapter for KaryakarniMemberAdapter
        val memberAdapter = KaryakarniMemberAdapter(model.members)
        holder.memberRecycler.adapter = memberAdapter
        holder.memberRecycler.layoutManager = LinearLayoutManager(holder.itemView.context)

        if (position == 0) {
            holder.memberRecycler.isVisible = true
            holder.memberRecycler.post {
                holder.memberRecycler.layoutParams.height = holder.memberRecycler.measuredHeight
            }
        } else {
            holder.memberRecycler.isVisible = false
        }


        holder.itemView.setOnClickListener {
            val shouldExpand = holder.memberRecycler.visibility != View.VISIBLE
            holder.memberRecycler.expandOrCollapse(shouldExpand, duration = 500)
        }
    }

    private fun View.expandOrCollapse(shouldExpand: Boolean, duration: Long = 500) {
        val initialHeight = if (shouldExpand) 0 else this.measuredHeight
        val targetHeight = if (shouldExpand) {
            measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            measuredHeight
        } else {
            0
        }

        if (shouldExpand) {
            this.layoutParams.height = 0
            this.isVisible = true
        }

        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            this.layoutParams.height = value
            this.requestLayout()
        }
        animator.duration = duration
        animator.start()

        if (!shouldExpand) {
            animator.addListener(onEnd = {
                this.isVisible = false
            })
        }
    }
}

package com.example.androidworrkshop.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.FamilyMemberItemLayoutBinding

class FamilyDetailsAdapter(private val context: Context, private val FamilyDetails : List<Member>) : RecyclerView.Adapter<FamilyDetailsAdapter.ViewHolder>() {


    class ViewHolder(binding: FamilyMemberItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.memberImage
        val name = binding.memberName
        val relation = binding.memberRelation
        val occupation = binding.memberOccupation
        var gender = binding.memberGender
        var dob = binding.memberDob
        var address = binding.memberAddress
        var phone = binding.callNow
        var genColor = binding.genderColor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FamilyMemberItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return FamilyDetails.size
    }

    override fun onBindViewHolder(holder: FamilyDetailsAdapter.ViewHolder, position: Int) {
        val model = FamilyDetails[position]
        holder.name.text = model.name
        holder.relation.text = model.relation
        holder.occupation.text = model.occupation
        if(model.gender == "Male"){
            holder.gender.setText("M")
        }else{
            holder.gender.setText("F")
        }
        holder.dob.text = model.DOB
        holder.address.text = model.address

        holder.phone.setOnClickListener{
            if(model.contact == "NA"){
                Toast.makeText(context,"Contact not available",Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${model.contact}")
                context.startActivity(intent)
            }

        }

        if (model.gender == "Male"){
            holder.genColor.setBackgroundColor(context.resources.getColor(R.color.male_card_color))
        }else{
            holder.genColor.setBackgroundColor(context.resources.getColor(R.color.female_card_color))
        }

        Glide.with(context)
            .load(model.profilePic)
            .circleCrop()
            .placeholder(R.drawable.baseline_person_24)
            .into(holder.image)

    }

}
package com.example.communityapp.ui.karyakarni

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.data.newModels.KaryaMember
import com.example.communityapp.data.newModels.Karyakarni
import de.hdodenhof.circleimageview.CircleImageView

class ExpandableListAdapter(
    private val context: Context,
    private val listDataHeader: List<Karyakarni>,
    private val listDataChild: Map<Karyakarni, List<KaryaMember>>
) : BaseExpandableListAdapter() {

    override fun getChild(listPosition: Int, expandedListPosition: Int): KaryaMember {
        return this.listDataChild[this.listDataHeader[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition)
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.child_item, null)
        }
        val memberName = convertView!!.findViewById<TextView>(R.id.member_name)
        val memberPosition = convertView.findViewById<TextView>(R.id.member_position)
        val memberImage = convertView.findViewById<CircleImageView>(R.id.nav_user_image)
        // Assuming you want to set an image resource here
        // memberImage.setImageResource(expandedListText.imageRes)

        var desig  = ""
        for (i in expandedListText.designations) {
            if (desig != "") {
                desig += ", "
            }
            desig += i
        }

        memberName.text = expandedListText.name
        memberPosition.text = desig

        Glide.with(context)
            .load(expandedListText.profilePic)
            .placeholder(R.drawable.chitranshlogo)
            .into(memberImage)

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.listDataChild[this.listDataHeader[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Karyakarni {
        return this.listDataHeader[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.listDataHeader.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        var convertView = convertView
        val headerTitle = getGroup(listPosition)
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.parent_item, null)
        }
        val level = convertView!!.findViewById<TextView>(R.id.karyakarni_level)
        val name = convertView.findViewById<TextView>(R.id.karyakarni_name)
        val address = convertView.findViewById<TextView>(R.id.karyakarni_Address)
        if(headerTitle.level == "India"){
            address.visibility = View.GONE
        }
        // Assuming you want to set an image resource here
        // logo.setImageResource(headerTitle.logo)
        level.text = headerTitle.level
        name.text = headerTitle.name
        address.text = headerTitle.address
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
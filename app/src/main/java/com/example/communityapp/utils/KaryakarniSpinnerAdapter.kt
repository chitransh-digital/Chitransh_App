package com.example.communityapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.communityapp.R
import com.example.communityapp.data.newModels.Karyakarni

class KaryakarniSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val items: List<Karyakarni>
) : ArrayAdapter<Karyakarni>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val item = getItem(position)

        val primaryText = view.findViewById<TextView>(R.id.primary_text)
        val secondaryText = view.findViewById<TextView>(R.id.secondary_text)

        if (position == 0) {
            view.findViewById<View>(R.id.karyaSpinnerLayoutSeperator).visibility = View.GONE
        }

        when (item?.level) {
            "India" -> {
                primaryText.text = "${item.name} - India"
                secondaryText.visibility = View.GONE
            }

            "State" -> {
                primaryText.text = item.name
                secondaryText.text = "State: ${item.state}"
                secondaryText.visibility = View.VISIBLE
            }

            "City" -> {
                primaryText.text = item.name
                secondaryText.text = "City: ${item.city}, ${item.state}"
                secondaryText.visibility = View.VISIBLE
            }

            else -> {
                if (item != null) {
                    primaryText.text = item.name
                }
                secondaryText.visibility = View.GONE
            }
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
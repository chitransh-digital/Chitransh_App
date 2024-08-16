package com.example.communityapp.ui.jobs

import android.content.Intent
import android.net.Uri
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.R
import com.example.communityapp.data.newModels.Job


class JobsAdapter(
    private val jobList: List<Job>,
    private val activity: JobsActivity,
    private val username: String
) :
    RecyclerView.Adapter<JobsAdapter.JobsViewHolder>() {
    lateinit var bottomSheetFragment: BottomSheetFragment

    inner class JobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jobPosition: TextView = itemView.findViewById(R.id.job_position)
        val companyName:TextView=itemView.findViewById(R.id.company_name)
        val jobDescription: TextView = itemView.findViewById(R.id.job_description)
        val contact: Button =itemView.findViewById(R.id.iv_contact)
        val salary : TextView = itemView.findViewById(R.id.salary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_item, parent, false)
        return JobsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        val currentItem = jobList[position]

        holder.jobPosition.text = currentItem.jobTitle
        holder.jobDescription.text = currentItem.jobDescription
//        holder.jobSalary.text = currentItem.first.salary.toString()
//        holder.jobLocation.text = currentItem.first.location
//        holder.requirements.text = currentItem.first.requirements.toString()
        if(currentItem.salary >= 0)holder.salary.text = currentItem.salary.toString()
        holder.companyName.text = currentItem.businessName
        holder.contact.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${currentItem.contact}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(activity.applicationContext,intent,null)
        }


        holder.itemView.setOnClickListener {
            try {
                bottomSheetFragment = BottomSheetFragment(currentItem,username) // send the id to fragment
                bottomSheetFragment.show(activity.supportFragmentManager, "BSDialogFragment")
            }
            catch (e:Exception) {
                e("error", "$e")
            }
        }
    }

    override fun getItemCount(): Int {
        return jobList.size
    }
}

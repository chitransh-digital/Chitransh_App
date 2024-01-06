package com.example.communityapp.ui.jobPosting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.Job
import com.example.communityapp.databinding.ActivityJobPostingBinding
import com.example.communityapp.ui.jobs.JobsViewModel
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobPostingActivity : AppCompatActivity() {
    private val jobsPostingViewModel:JobPostingViewModel by viewModels()
    private lateinit var binding : ActivityJobPostingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservables()

        binding.JobSubmit.setOnClickListener {
            checkDetails()
        }

    }

    private fun checkDetails() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.jobTitleinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter Title of job", Toast.LENGTH_SHORT).show()
        }else if(binding.salaryinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter the Salary", Toast.LENGTH_SHORT).show()
        }else if(binding.locationinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter location of the Job", Toast.LENGTH_SHORT).show()
        }
        else if(binding.reqinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter Job requirements", Toast.LENGTH_SHORT).show()
        }
        else if(binding.Descinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter the Description", Toast.LENGTH_SHORT).show()
        }
        else{
            submitRegistration()
        }
    }

    private fun submitRegistration() {

        val req_list = binding.reqinput.text.split(" ")

        val data = Job(
            businessName = binding.nameinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            jobDescription = binding.Descinput.text.toString(),
            jobTitle = binding.jobTitleinput.text.toString(),
            requirements = req_list,
            salary = binding.salaryinput.text.toString().toInt(),
            location = binding.locationinput.text.toString()
        )

        jobsPostingViewModel.addJob(data)
    }

    private fun setObservables(){
        jobsPostingViewModel.jobAddedResult.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    Log.e("Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }
}
package com.example.communityapp.ui.jobPosting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.communityapp.R
import com.example.communityapp.ui.jobs.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobPostingActivity : AppCompatActivity() {
    private val jobsPostingViewModel:JobPostingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_posting)


    }
}
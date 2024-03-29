package com.example.communityapp.ui.jobs

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.models.Job
import com.example.communityapp.databinding.ActivityJobsBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class JobsActivity : BaseActivity() {

    private val jobsViewModel: JobsViewModel by viewModels()
    private lateinit var binding : ActivityJobsBinding
    private var username:String = "NA"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgressDialog("Fetching Jobs...")
        jobsViewModel.getAllJobs()

//        jobsViewModel.deleteJob("b")

        val currentDateTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Format LocalDateTime to a string
        val formattedDateTime = currentDateTime.format(formatter)

//        jobsViewModel.addComment(
//            "QqDncH9qQNzPTv9RVY0m",
//            Comment("a", formattedDateTime,"this is a comment4")
//        )

//        jobsViewModel.getAllComments("QqDncH9qQNzPTv9RVY0m")

//        jobsViewModel.addJob(
//            Job("b","87886785","afa","FSDASDA", listOf("a","b","c"),765,"jabalpur")
//        )

        binding.jobBack.setOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(Constants.NAME)){
            username = intent.getStringExtra(Constants.NAME).toString()
         }

        setObservables()
        setWindowsUp()
    }

    private fun setObservables() {
        //observe get all jobs

        jobsViewModel.jobResult.observe(this, Observer { resource ->
            hideProgressDialog()
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    val jobs = resource.data
                    Log.e("All jobs", "$jobs")
                    if (jobs != null) {
                        setupRecyclerView(jobs)
                    }
                    // Update UI or perform any actions with the list of jobs
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    showErrorSnackBar("Error: ${resource.apiError?.message}")
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                }
            }
        })

        //observe add job
        jobsViewModel.jobAddedResult.observe(this, Observer { result ->
            when (result.status) {
                Resource.Status.LOADING -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        //observe delete job
        jobsViewModel.jobDeletedResult.observe(this, Observer { result ->
            when (result.status) {
                Resource.Status.LOADING -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        //observe add comment
        jobsViewModel.commentAddedResult.observe(this, Observer { result ->
            when (result.status) {
                Resource.Status.LOADING -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Comment Success", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        //observe get all comments
        jobsViewModel.commentsResult.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    val comments = resource.data
                    Log.d("All comments", "$comments")
                    // Update UI or perform any actions with the list of comments
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                }
            }
        })

    }

    private fun setupRecyclerView(jobs: List<Pair<Job,String>>){
        Log.d("Recycler view Job",jobs.toString())
        val jobsAdapter = JobsAdapter(jobs, this@JobsActivity,username)
        binding.rvJobs.adapter = jobsAdapter
        binding.rvJobs.layoutManager = LinearLayoutManager(this)
    }
}
package com.example.communityapp.ui.jobs

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
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
    private var jobsList = mutableListOf<com.example.communityapp.data.newModels.Job>()
    private lateinit var jobsAdapter: JobsAdapter
    private val limit =10
    private var page = 1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgressDialog(getString(R.string.fetching_jobs))
        setupRecyclerView(jobsList)
        jobsViewModel.getAllJobs(limit, page)

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
                        jobsList.addAll(jobs.jobs)
                        jobsAdapter.notifyDataSetChanged()
                    }
                }
                Resource.Status.ERROR -> {
                    val error = resource.apiError
                    showErrorSnackBar("${resource.apiError?.message}")
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
                    Toast.makeText(this, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        //observe delete job
        jobsViewModel.jobDeletedResult.observe(this, Observer { result ->
            when (result.status) {
                Resource.Status.LOADING -> {
                    Toast.makeText(this, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                }
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
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

    private fun setupRecyclerView(jobs: List<com.example.communityapp.data.newModels.Job>){
        Log.d("Recycler view Job",jobs.toString())
        jobsAdapter = JobsAdapter(jobs, this@JobsActivity,username)
        binding.rvJobs.adapter = jobsAdapter
        binding.rvJobs.layoutManager = LinearLayoutManager(this)

        // Pagination logic
        binding.rvJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (totalItemCount <= (lastVisibleItem + 2)) {
                    // If the user has scrolled to the end of the list, load more data
                    loadMoreData()
                }
            }
        })

    }

    private fun loadMoreData() {
        page++
        jobsViewModel.getAllJobs(limit, page)
    }
}
package com.example.communityapp.ui.jobs

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.communityapp.R
import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.ui.feed.FeedsAdapter
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class JobsActivity : AppCompatActivity() {

    private val jobsViewModel: JobsViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

//        jobsViewModel.getAllJobs()

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
//            Job("b","87886785","afa","FSDASDA", listOf("a","b","c"),765)
//        )

        setObservables()
    }

    private fun setObservables() {
        //observe get all jobs

        jobsViewModel.jobResult.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    val jobs = resource.data
                    Log.e("All jobs", "$jobs")
                    // Update UI or perform any actions with the list of jobs
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
}
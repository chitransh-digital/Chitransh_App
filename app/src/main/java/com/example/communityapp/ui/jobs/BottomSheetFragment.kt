package com.example.communityapp.ui.jobs

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.Comment
import com.example.communityapp.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BottomSheetFragment(private var jobId:String) : BottomSheetDialogFragment() {
    private val jobsViewModel: JobsViewModel by activityViewModels()
    private var comments:MutableList<Comment> = mutableListOf()
    private lateinit var commentsAdapter: CommentsAdapter
    var lastCommentAdded:Comment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservables()

    try {
        jobsViewModel.getAllComments(jobId)
    }
    catch (e:Exception) {
        e("error", "$e")
    }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)

        rootView.findViewById<ImageView>(R.id.submitComment)?.setOnClickListener {
            e("comment", "clicked")
            if (rootView.findViewById<EditText>(R.id.commentInput)?.text?.isNotEmpty() == true) {
                val currentDateTime = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                // Format LocalDateTime to a string
                val formattedDateTime = currentDateTime.format(formatter)
                lastCommentAdded = Comment("a", formattedDateTime, rootView.findViewById<EditText>(R.id.commentInput)?.text.toString())

                jobsViewModel.addComment(
                    jobId,
                    lastCommentAdded!!
                )
                rootView.findViewById<EditText>(R.id.commentInput)?.text?.clear()
            } else {
                Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun setObservables() {
        //observe get all comments
        jobsViewModel.commentsResult.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                      resource.data?.forEach{
                          comments.add(it)
                      }

                    setupRv()
                    Log.d("All commentsi", "$comments")

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

        //observe add comment
        jobsViewModel.commentAddedResult.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    val comment = resource.data
                    Log.e("Added comment", "$comment")
                    try {
                        if (comment == true) {
                            comments.add(lastCommentAdded!!)
                            commentsAdapter.notifyDataSetChanged()
                        }
                    }catch (e:Exception) {
                        e.printStackTrace()
                    }

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

    private fun setupRv(){
        //setup recycler view
        val rv= view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.commentRecyclerView)

         commentsAdapter = CommentsAdapter(comments)
        rv?.adapter = commentsAdapter
        rv?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

}
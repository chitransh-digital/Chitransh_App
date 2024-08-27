package com.example.communityapp.ui.jobs

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.newModels.Job
import com.example.communityapp.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BottomSheetFragment(private var jobId:Job,private val username:String) : BottomSheetDialogFragment() {
    private val jobsViewModel: JobsViewModel by activityViewModels()
    private var comments:MutableList<Comment> = mutableListOf()
    private lateinit var commentsAdapter: CommentsAdapter
    var lastCommentAdded:Comment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservables()

    try {
//        jobsViewModel.getAllComments(jobId.second)
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

        rootView.findViewById<TextView>(R.id.tvMore).movementMethod = ScrollingMovementMethod()

        rootView.findViewById<TextView>(R.id.tvMore).text = jobId.jobDescription
        rootView.findViewById<TextView>(R.id.tv_expandedJobTitle).text = jobId.jobTitle
        rootView.findViewById<TextView>(R.id.jobExpandedLocation).text = jobId.location
        if (jobId.contact != "0000000000"){
            rootView.findViewById<TextView>(R.id.jobExpandedAllContact).text = jobId.contact
        }
        if (jobId.salary == 0){
            rootView.findViewById<TextView>(R.id.jobExpandedSalary).text =
                getString(R.string.not_specified)
        }else{
            rootView.findViewById<TextView>(R.id.jobExpandedSalary).text = jobId.salary.toString()
        }

        rootView.findViewById<ImageView>(R.id.submitComment)?.setOnClickListener {
            if (rootView.findViewById<EditText>(R.id.commentInput)?.text?.isNotEmpty() == true) {
                val currentDateTime = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                // Format LocalDateTime to a string
                val formattedDateTime = currentDateTime.format(formatter)
                lastCommentAdded = Comment(username, formattedDateTime, rootView.findViewById<EditText>(R.id.commentInput)?.text.toString())

//                jobsViewModel.addComment(
//                    jobId.second,
//                    lastCommentAdded!!
//                )
                rootView.findViewById<EditText>(R.id.commentInput)?.text?.clear()
            } else {
                Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        rootView.findViewById<Button>(R.id.btn_jobLink).setOnClickListener {
            if (jobId.externalLink.isEmpty()) {
                Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var url = jobId.externalLink.trim()

            if (url.startsWith("H")){
                url = url.replaceFirst("H", "h")
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(context, "Invalid URL" + url, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            val chooser = Intent.createChooser(intent, "Open link with")
            startActivity(chooser)
//            if (context?.let { it1 -> intent.resolveActivity(it1.packageManager) } != null) {
//                startActivity(chooser)
//            } else {
//                Toast.makeText(context, "No app found to open this link", Toast.LENGTH_SHORT).show()
//            }

        }

        rootView.findViewById<Button>(R.id.btn_jobCall) .setOnClickListener {
            if(jobId.contact.isEmpty() || jobId.contact == "0000000000"){
                Toast.makeText(context, "No contact available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${jobId.contact}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context?.let { it1 -> ContextCompat.startActivity(it1, intent, null) }
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

        view?.let { ViewCompat.setNestedScrollingEnabled(it, true) };

         commentsAdapter = CommentsAdapter(comments)
        rv?.adapter = commentsAdapter
        rv?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

}
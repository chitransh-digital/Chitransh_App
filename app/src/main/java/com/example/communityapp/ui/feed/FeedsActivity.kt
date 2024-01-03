package com.example.communityapp.ui.feed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.e
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.ActivityFeedsBinding
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.ui.jobs.JobsViewModel
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedsActivity : AppCompatActivity() {

    private val feedsViewModel: FeedsViewModel by viewModels()
    private val selectedImagePaths = mutableListOf<String>()
    private lateinit var binding : ActivityFeedsBinding
    private var lastNews: NewsFeed? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    // Handle the selected image
                    val selectedImageUri = intent.data
                    val selectedImagePath = selectedImageUri?.let { getImagePath(it) }

                    if (selectedImagePath != null) {
                        // Add the selected image path to the list
                        selectedImagePaths.add(selectedImagePath)
                        // UpdateUI()
                        e("selectedImagePath", selectedImagePaths.toString())
                        feedsViewModel.addFeed(
                            NewsFeed(
                                "author1",
                                "fSDsfSfsdfgdf",
                                emptyList(),
                                "timestamp",
                                "title1",
                                true
                            ),
                            selectedImagePaths
                        )
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFeeds.setOnClickListener {
            openFilePicker()
        }

        ///getFeedsbyPaging
        if(lastNews == null){
            feedsViewModel.getFeedsByPaging()
        }else{
            feedsViewModel.getFeedsByPaging(lastNews)
        }


        setObservables()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        getContent.launch(intent)
    }

    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private fun setObservables() {

        feedsViewModel.addFeedResult.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    Toast.makeText(this, error?.message, Toast.LENGTH_SHORT).show()
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                    Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        feedsViewModel.feeds.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                    e("feeds", resource.data.toString())
                    lastNews=resource.data?.last()
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    Toast.makeText(this, error?.message, Toast.LENGTH_SHORT).show()
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                    Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

    }
}
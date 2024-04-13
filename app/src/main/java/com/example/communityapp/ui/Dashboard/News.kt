package com.example.communityapp.ui.Dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.FragmentNewsBinding
import com.example.communityapp.ui.feed.FeedsAdapter
import com.example.communityapp.ui.feed.FeedsViewModel
import com.example.communityapp.utils.Resource

class News : Fragment() {
    private val feedsViewModel: FeedsViewModel by activityViewModels()
    private val selectedImagePaths = mutableListOf<String>()
    private lateinit var binding : FragmentNewsBinding
    private var lastNews: NewsFeed? = null
    private  var feedsList:MutableList<NewsFeed> = mutableListOf()
    private lateinit var newsAdapter: FeedsAdapter
    private var position: Int = -1  // Default value or a value that indicates it's not set

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
                        Log.e("selectedImagePath", selectedImagePaths.toString())
                        feedsViewModel.addFeed(
                            NewsFeed(
                                "author3",
                                "fSDsfSfsdfgdf",
                                emptyList(),
                                "timestamp",
                                "title3",
                                true,
                                "location"
                            ),
                            selectedImagePaths
                        )
                    }
                }
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

//        binding.btnFeeds.setOnClickListener {
//            openFilePicker()
//        }

        //getFeedsbyPaging
//        if(lastNews == null){
//            showAdapter(feedsList)
//            feedsViewModel.getFeedsByPaging()
//        }else{
//            feedsViewModel.getFeedsByPaging(lastNews)
//        }



        if(arguments!=null){
            arguments?.let {
                e("h","hihihi")
                position = it.getInt("position", -1)
                val args = it.getParcelableArrayList<NewsFeed>("newsFeedList") ?: mutableListOf()
                lastNews = args.last()
                feedsList = args.toMutableList()
                showAdapter(feedsList)
            }
        }else{
            if(lastNews == null){
                showAdapter(feedsList)
                feedsViewModel.getFeedsByPaging()
            }else{
                feedsViewModel.getFeedsByPaging(lastNews)
            }
        }


        setObservables()

        // Inflate the layout for this fragment
        return binding.root
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
        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private fun setObservables() {

        feedsViewModel.addFeedResult.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
//                    Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    Toast.makeText(requireContext(), error?.message, Toast.LENGTH_SHORT).show()
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

        feedsViewModel.feeds.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // Handle success state
                    Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                    Log.e("feeds", resource.data.toString())

                    if(resource.data?.isNotEmpty() == true) {
                        lastNews = resource?.data.last()
                    }
                    resource.data?.forEach{
                        feedsList.add(it)
                    }
//                    if(lastNews==null) {
//                        showAdapter(feedsList)
//                    }

                    newsAdapter.notifyDataSetChanged()
                }
                Resource.Status.ERROR -> {
                    // Handle error state
                    val error = resource.apiError
                    Toast.makeText(requireContext(), error?.message, Toast.LENGTH_SHORT).show()
                    // Handle the error, show a message or retry
                }
                Resource.Status.LOADING -> {
                    // Handle loading state
                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        })

    }

    fun showAdapter(feedsList:List<NewsFeed>){
        val viewPager: ViewPager2 = binding.viewPager
        newsAdapter = FeedsAdapter(feedsList, requireContext())
        viewPager.adapter = newsAdapter
        viewPager.setCurrentItem(position, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Check if the last item is reached
                if (position == feedsList.size - 1) {
                    // Call  method to fetch more feeds
                    if(lastNews!=null)feedsViewModel.getFeedsByPaging(lastNews)
                }
            }
        })
    }

}
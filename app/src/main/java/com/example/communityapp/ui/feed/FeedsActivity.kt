package com.example.communityapp.ui.feed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.ActivityFeedsBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedsActivity : BaseActivity() {

    private val feedsViewModel: FeedsViewModel by viewModels()
    private val selectedImagePaths = mutableListOf<String>()
    private lateinit var binding : ActivityFeedsBinding
    private var lastNews: NewsFeed? = null
    private  var feedsList:MutableList<NewsFeed> = mutableListOf()
    private lateinit var newsAdapter: FeedsAdapter
    private val limit =5
    private var page = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnFeeds.setOnClickListener {
//            openFilePicker()
//        }
        setWindowsUp()
        //getFeedsbyPaging
        showAdapter(feedsList)
        feedsViewModel.getFeedsByPaging(limit, page)


        setObservables()
    }



    private fun setObservables() {

        feedsViewModel.feeds.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                    resource.data?.let { data ->
                        feedsList.addAll(data.Feeds)
                        newsAdapter.notifyDataSetChanged()
                        page++
                    }
                }
                Resource.Status.ERROR -> {
                    val error = resource.apiError
                    Toast.makeText(this, error?.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
                    Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    fun showAdapter(feedsList:List<NewsFeed>){
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
         newsAdapter = FeedsAdapter(feedsList, this)
        viewPager.adapter = newsAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == feedsList.size - 1) {
                    feedsViewModel.getFeedsByPaging(limit, page)
                }
            }
        })
    }

}
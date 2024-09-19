package com.example.communityapp.ui.Dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
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
    private val limit =10
    private var page = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)


        if (arguments != null) {
            arguments?.let {
                position = it.getInt("position", -1)
                val args = it.getParcelableArrayList<NewsFeed>("newsFeedList") ?: mutableListOf()
                feedsList = args.toMutableList()
                showAdapter(feedsList)
            }
        } else {
            showAdapter(feedsList)
            feedsViewModel.getFeedsByPaging(limit, page)
        }


        setObservables()

        // Inflate the layout for this fragment
        return binding.root
    }



    private fun setObservables() {

        feedsViewModel.feeds.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
//                    Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                    resource.data?.let { data ->
                        feedsList.addAll(data.Feeds)
                        newsAdapter.notifyDataSetChanged()
                    }
                }
                Resource.Status.ERROR -> {
                    val error = resource.apiError
                    Toast.makeText(requireContext(), error?.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
//                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                }
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

                // Notify the adapter to reset the scroll position for the new page
                val viewHolder = (viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(position)
                if (viewHolder is FeedsAdapter.NewsViewHolder) {
                    viewHolder.scrollView.scrollTo(0, 0)
                }

                if (position == feedsList.size - 1) {
                    feedsViewModel.getFeedsByPaging(limit, page)
                }
            }
        })

    }

}
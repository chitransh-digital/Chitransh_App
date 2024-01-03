package com.example.communityapp.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.data.repository.FeedsRepo
import com.example.communityapp.data.repository.JobsRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(private  val feedsRepo: FeedsRepo): ViewModel() {

    private val _addFeedResult = MutableLiveData<Resource<Unit>>()
    val addFeedResult: LiveData<Resource<Unit>>
        get() = _addFeedResult

    fun addFeed(feedData: NewsFeed, imagePaths: List<String>) {
        _addFeedResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                feedsRepo.addFeed(feedData, imagePaths)
                _addFeedResult.postValue(Resource.success(Unit))
            } catch (e: Exception) {
                _addFeedResult.postValue(Resource.error(e))
            }
        }
    }

    //getFeedsByPaging
    private val _feeds = MutableLiveData<Resource<List<NewsFeed>>>()
    val feeds: LiveData<Resource<List<NewsFeed>>>
        get() = _feeds

    fun getFeedsByPaging(lastFeed: NewsFeed? = null) {
        _feeds.value = Resource.loading()
        viewModelScope.launch {
            try {
                val feeds = feedsRepo.getFeeds(lastFeed)
                _feeds.postValue(Resource.success(feeds))
            } catch (e: Exception) {
                _feeds.postValue(Resource.error(e))
            }
        }
    }
}
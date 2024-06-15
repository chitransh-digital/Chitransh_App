package com.example.communityapp.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.data.newModels.FeedsResponse
import com.example.communityapp.data.repository.FeedsRepo
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
    private val _feeds = MutableLiveData<Resource<FeedsResponse>>()
    val feeds: LiveData<Resource<FeedsResponse>>
        get() = _feeds

    fun getFeedsByPaging(limit:Int,page: Int) {
        _feeds.value = Resource.loading()
        viewModelScope.launch {
            try {
                val response = feedsRepo.getNewFeeds(limit,page)
                if (response.isSuccessful) {
                    _feeds.postValue(Resource.success(response.body()!!))
                }else if(response.code()==400){
                    _feeds.postValue(Resource.error(Exception("No more news available")))
                }
                else {
                    _feeds.postValue(Resource.error(Exception(response.message())))
                }
            } catch (e: Exception) {
                _feeds.postValue(Resource.error(e))
            }
        }
    }
}
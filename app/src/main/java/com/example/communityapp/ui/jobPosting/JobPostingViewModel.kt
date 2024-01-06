package com.example.communityapp.ui.jobPosting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.repository.JobPostingRepo
import com.example.communityapp.data.repository.JobsRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobPostingViewModel@Inject constructor(private  val jobPostingRepo: JobPostingRepo): ViewModel() {

    //addJob
    private val _jobAddedResult = MutableLiveData<Resource<Boolean>>()
    val jobAddedResult: LiveData<Resource<Boolean>>
        get() = _jobAddedResult

    fun addJob(job: Job) {
        _jobAddedResult.value = Resource.loading()
        Log.e("JobsViewModel", "addJob")
        viewModelScope.launch {
            try {
                jobPostingRepo.addJob(job)
                _jobAddedResult.postValue(Resource.success(true))
            } catch (e: Exception) {
                Log.e("JobsViewModel", "addJob: ${e.message}")
                _jobAddedResult.postValue(Resource.error(e))
            }
        }
    }
}
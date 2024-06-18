package com.example.communityapp.ui.jobs

import android.os.Build
import android.util.Log
import android.util.Log.e
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.newModels.JobsResponse
import com.example.communityapp.data.repository.JobsRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(private  val jobsRepo: JobsRepo): ViewModel(){

    //addJob
    private val _jobAddedResult = MutableLiveData<Resource<Boolean>>()
    val jobAddedResult: LiveData<Resource<Boolean>>
        get() = _jobAddedResult

    fun addJob(job: Job) {
        _jobAddedResult.value = Resource.loading()
        e("JobsViewModel", "addJob")
        viewModelScope.launch {
            try {
                jobsRepo.addJob(job)
                _jobAddedResult.postValue(Resource.success(true))
            } catch (e: Exception) {
                Log.e("JobsViewModel", "addJob: ${e.message}")
                _jobAddedResult.postValue(Resource.error(e))
            }
        }
    }


    private val _jobResult = MutableLiveData<Resource<JobsResponse>>()
    val jobResult: LiveData<Resource<JobsResponse>>
        get() = _jobResult

    // Function to fetch all jobs
    fun getAllJobs(limit: Int = 10, page: Int) {
        _jobResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                val response = jobsRepo.getJobs(limit,page)
                if(response.isSuccessful){
                    _jobResult.postValue(Resource.success(response.body()))
                }else if(response.code()==400){
                    _jobResult.postValue(Resource.error(Exception("All jobs shown")))
                }
                else{
                    _jobResult.postValue(Resource.error(Exception(response.message())))
                }
            } catch (e: Exception) {
                _jobResult.postValue(Resource.error(e))
            }
        }
    }

    //fun delete by job  id
    private val _jobDeletedResult = MutableLiveData<Resource<Boolean>>()
    val jobDeletedResult: LiveData<Resource<Boolean>>
        get() = _jobDeletedResult

    fun deleteJob(jobId: String) {
        _jobDeletedResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                jobsRepo.deleteJobById(jobId)
                _jobDeletedResult.postValue(Resource.success(true))
            } catch (e: Exception) {
                _jobDeletedResult.postValue(Resource.error(e))
            }
        }
    }

    //fun add comment in a job
    private val _commentAddedResult = MutableLiveData<Resource<Boolean>>()
    val commentAddedResult: LiveData<Resource<Boolean>>
        get() = _commentAddedResult

    fun addComment(jobId: String, comment: Comment) {
        _commentAddedResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                jobsRepo.addComment(jobId, comment)
                _commentAddedResult.postValue(Resource.success(true))
            } catch (e: Exception) {
                _commentAddedResult.postValue(Resource.error(e))
            }
        }
    }

    //get all comments
    private val _commentsResult = MutableLiveData<Resource<List<Comment>>>()
    val commentsResult: LiveData<Resource<List<Comment>>>
        get() = _commentsResult

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllComments(jobId: String) {
        _commentsResult.value = Resource.loading()
        viewModelScope.launch {
            try {
                val comments = jobsRepo.getAllComments(jobId)
                _commentsResult.postValue(Resource.success(comments))
            } catch (e: Exception) {
                _commentsResult.postValue(Resource.error(e))
            }
        }
    }



}
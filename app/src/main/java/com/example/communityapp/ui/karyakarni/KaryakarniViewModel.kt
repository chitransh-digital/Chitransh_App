package com.example.communityapp.ui.karyakarni

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.newModels.JobsResponse
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.repository.JobsRepo
import com.example.communityapp.data.repository.KaryakarniRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KaryakarniViewModel @Inject constructor(private  val karyakarniRepo: KaryakarniRepo): ViewModel(){


    private val _karyakarni_list = MutableLiveData<Resource<KaryakarniResponse>>()
    val karyakarni_list: LiveData<Resource<KaryakarniResponse>>
        get() = _karyakarni_list

    // Function to fetch all jobs
    fun getAllKaryakarni(limit: Int = 10, page: Int) {
        _karyakarni_list.value = Resource.loading()
        viewModelScope.launch {
            try {
                val response = karyakarniRepo.getKaryakarni(limit,page)
                if(response.isSuccessful){
                    _karyakarni_list.postValue(Resource.success(response.body()))
                }else if(response.code()==400){
                    _karyakarni_list.postValue(Resource.error(Exception("All Karyakarni shown")))
                }
                else{
                    _karyakarni_list.postValue(Resource.error(Exception(response.message())))
                }
            } catch (e: Exception) {
                _karyakarni_list.postValue(Resource.error(e))
            }
        }
    }

}
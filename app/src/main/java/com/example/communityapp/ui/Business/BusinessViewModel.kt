package com.example.communityapp.ui.Business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Business
import com.example.communityapp.data.repository.BusinessRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(private var businessRepo: BusinessRepo) : ViewModel() {

    private val _business = MutableLiveData<Resource<String>>()

    val business : LiveData<Resource<String>>
        get() = _business

    fun addBusiness(business: Business){
        _business.value = Resource.loading()
        viewModelScope.launch {
            try{
                val business = businessRepo.addBusiness(business)
                _business.value = Resource.success("Done")
            }catch (e : Exception){
                _business.value = Resource.error(e)
            }
        }
    }

}
package com.example.communityapp.ui.Business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.newModels.AddBusinessResponse
import com.example.communityapp.data.newModels.BusinessResponse
import com.example.communityapp.data.repository.BusinessRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(private var businessRepo: BusinessRepo) : ViewModel() {

    private val _business = MutableLiveData<Resource<AddBusinessResponse>>()

    val business : LiveData<Resource<AddBusinessResponse>>
        get() = _business

    fun addBusiness(business:com.example.communityapp.data.newModels.Business, multiPartList: MutableList<MultipartBody.Part>, multiPartFile: MultipartBody.Part?){
        _business.value = Resource.loading()
        viewModelScope.launch {
            try{
                var imageList = mutableListOf<String>()
                for(i in multiPartList){
                    val imageLink= businessRepo.uploadImage(i)
                    if(imageLink.isSuccessful) imageLink.body()?.file?.let { imageList.add(it) }
                    else {
                        _business.value = Resource.error(Exception(imageLink.message()))
                        return@launch
                    }
                }

                business.images = imageList

                if(multiPartFile != null){
                    val fileLink = businessRepo.uploadImage(multiPartFile)
                    if(fileLink.isSuccessful) business.attachments = listOf(fileLink.body()?.file!!)
                    else {
                        _business.value = Resource.error(Exception(fileLink.message()))
                        return@launch
                    }
                }

                val response = businessRepo.addBusiness(business)

                if(response.isSuccessful){
                    _business.value = Resource.success(response.body())
                }else{
                    _business.value = Resource.error(Exception(response.message()))
                }
            }catch (e : Exception){
                _business.value = Resource.error(e)
            }
        }
    }

    private val _business_list = MutableLiveData<Resource<BusinessResponse>>()

    val business_list : LiveData<Resource<BusinessResponse>>
        get() = _business_list

    fun getBusiness(limit : Int, page : Int){
        _business_list.value = Resource.loading()
        viewModelScope.launch {
            try{
                val response = businessRepo.getBusinesses(limit,page)
                if(response.isSuccessful){
                    _business_list.value = Resource.success(response.body())
                }else if(response.code() == 400){
                    _business_list.value = Resource.error(Exception("No more businesses"))
                }
                else{
                    _business_list.value = Resource.error(Exception(response.message()))
                }
            }catch (e : Exception){
                _business_list.value = Resource.error(e)
            }
        }
    }

}
package com.example.communityapp.ui.family

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.FamilyResponse
import com.example.communityapp.data.repository.FamilyRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(private var familyRepo: FamilyRepo) : ViewModel() {

    //add Member
    private val _user = MutableLiveData<Resource<String>>()
    val user: LiveData<Resource<String>>
        get() = _user

    fun addMember(member: Member,selectedImagePath:String) {
        familyRepo.addMember(member,selectedImagePath).onEach {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    _user.value = Resource.success(it.data)
                }
                Resource.Status.LOADING -> {
                    _user.value = Resource.loading()
                }
                Resource.Status.ERROR -> {
                    _user.value = Resource.error(it.apiError)
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }


    private val _user_data = MutableLiveData<Resource<List<List<Member>>>>()

    val user_data : LiveData<Resource<List<List<Member>>>>
        get() = _user_data

    fun getFamilyByCity(){
        _user_data.value = Resource.loading()
        viewModelScope.launch {
            try{
                val user = familyRepo.getAllFamily()
                _user_data.value = Resource.success(user)
            }catch (e : Exception){
                _user_data.value = Resource.error(e)
            }
        }
    }


    private val _family = MutableLiveData<Resource<FamilyResponse>>()

    val family : LiveData<Resource<FamilyResponse>>
        get() = _family

    fun getAllFamily(limit:Int,page:Int){
        _family.value = Resource.loading()
        viewModelScope.launch {
            try{
                val response = familyRepo.getAllFamilies(limit,page)
                if(response.isSuccessful){
                    _family.value = Resource.success(response.body())
                }else if(response.code() == 404){
                    _family.value = Resource.error(Exception(Constants.Error404))
                }
                else if(response.code() == 400){
                    _family.value = Resource.error(Exception("no more families to show"))
                }
                else{
                    _family.value = Resource.error( Exception(response.message()))
                }
            }catch (e : Exception){
                _family.value = Resource.error(e)
            }
        }
    }



}
package com.example.communityapp.ui.family

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.repository.FamilyRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

}
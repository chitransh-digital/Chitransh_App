package com.example.communityapp.ui.Dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.repository.DashboardRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private var dashboardRepo: DashboardRepo) : ViewModel() {

    private val _user_data = MutableLiveData<Resource<Member>>()

    val user_data : LiveData<Resource<Member>>
        get() = _user_data

    fun getMember(contact : String){
        _user_data.value = Resource.loading()
        viewModelScope.launch {
            try{
                val user = dashboardRepo.findMember(contact)
                _user_data.value = Resource.success(user)
            }catch (e : Exception){
                _user_data.value = Resource.error(e)
            }
        }
    }

}
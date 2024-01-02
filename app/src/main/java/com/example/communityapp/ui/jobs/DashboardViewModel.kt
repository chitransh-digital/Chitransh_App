package com.example.communityapp.ui.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.repository.DashboardRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private var dashboardRepo: DashboardRepo) : ViewModel() {

    private val _user_data = MutableLiveData<Resource<Map<String, String>>>()

    val user_data : LiveData<Resource<Map<String, String>>>
        get() = _user_data

    fun getMember(contact : String){
//        dashboardRepo.findMember(contact).onEach {
//            when(it.status){
//                Resource.Status.SUCCESS -> {
//                    _user_data.value = Resource.success(it.data)
//                }
//                Resource.Status.LOADING -> {
//                    _user_data.value = Resource.loading()
//                }
//                Resource.Status.ERROR -> {
//                    _user_data.value = Resource.error(it.apiError)
//                }
//                else -> {}
//            }
//        }.launchIn(viewModelScope)
    }

}
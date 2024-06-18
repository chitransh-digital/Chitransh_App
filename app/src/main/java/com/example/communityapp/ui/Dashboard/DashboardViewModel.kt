package com.example.communityapp.ui.Dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.FamilyResponse
import com.example.communityapp.data.newModels.FeedsResponse
import com.example.communityapp.data.repository.DashboardRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private var dashboardRepo: DashboardRepo) : ViewModel() {

    private val _user_data = MutableLiveData<Resource<FamilyResponse>>()

    val user_data : LiveData<Resource<FamilyResponse>>
        get() = _user_data

    fun getMember(contact : String){
        _user_data.value = Resource.loading()
        viewModelScope.launch {
            try{
                val response = dashboardRepo.findFamilyByContact(contact)
                if(response.isSuccessful){
                    _user_data.value = Resource.success(response.body())
                }else if(response.code() == 404){
                    _user_data.value = Resource.error(Exception(Constants.Error404))
                }else if(response.code() == 401){
                    signInWithPhone(contact)
                }
                else{
                    _user_data.value = Resource.error( Exception(response.message()))
                }
            }catch (e : Exception){
                _user_data.value = Resource.error(e)
            }
        }
    }

    private val _loginStatusPhone = MutableLiveData<Resource<LoginResponse>>()
    val loginStatusPhone: LiveData<Resource<LoginResponse>>
        get() = _loginStatusPhone
    fun signInWithPhone(phone: String) {
        _loginStatusPhone.value = Resource.loading()
        viewModelScope.launch {
            try{
                val res = dashboardRepo.refreshToken(phone)
                if(res.isSuccessful){
                    _loginStatusPhone.value = Resource.success(res.body())
                }else if(res.code() == 404){
                    _loginStatusPhone.value = Resource.error(Exception(Constants.Error404))
                }
            }catch (e : Exception){
                _loginStatusPhone.value = Resource.error(e)
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
                val response = dashboardRepo.getNewFeeds(limit,page)
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


    private val _updatedUser = MutableLiveData<Resource<String>>()
    val updatedUser: LiveData<Resource<String>>
        get() = _updatedUser

    fun updateMember(memberId: String, updatedMember: Member, selectedImagePath: String?, change : Boolean) {
        dashboardRepo.updateMember(memberId, updatedMember, selectedImagePath, change).onEach {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    _updatedUser.value = Resource.success(it.data)
                }
                Resource.Status.LOADING -> {
                    _updatedUser.value = Resource.loading()
                }
                Resource.Status.ERROR -> {
                    _updatedUser.value = Resource.error(it.apiError)
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private var _deleteUser = MutableLiveData<Resource<Unit>>()
    val deleteUser: LiveData<Resource<Unit>>
        get() = _deleteUser

    fun deleteMember(familyId : String, contact : String) {
        _deleteUser.value = Resource.loading()
        viewModelScope.launch {
            try {
                val res = dashboardRepo.deleteMember(familyId,contact)
                _deleteUser.value = Resource.success(res)
            }catch (e : Exception){
                _deleteUser.value = Resource.error(e)
            }
        }
    }


}
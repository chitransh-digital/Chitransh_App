package com.example.communityapp.ui.Dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.FamilyResponse
import com.example.communityapp.data.newModels.FeedsResponse
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.newModels.addMember
import com.example.communityapp.data.repository.DashboardRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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

    fun updateMember(updatedMember: addMember, selectedImagePath: MultipartBody.Part?,
                     familyHash : String) {
        _updatedUser.value = Resource.loading()
        viewModelScope.launch {
            try {
                if(selectedImagePath != null){
                    val res = dashboardRepo.uploadImage(selectedImagePath)
                    if(res.isSuccessful){
                        updatedMember.memberData.profilePic = res.body()?.file!!
                    }else{
                        _updatedUser.value = Resource.error(Exception(res.message()))
                        return@launch
                    }
                }

                val response = dashboardRepo.updateMember(updatedMember,familyHash,
                    updatedMember.memberData._id)

                if (response.isSuccessful) {
                    _updatedUser.value = Resource.success(response.body()?.message)
                } else {
                    _updatedUser.value = Resource.error(Exception(response.message()))
                }
            } catch (e: Exception) {
                _updatedUser.value = Resource.error(e)
            }
        }
    }

    private var _deleteUser = MutableLiveData<Resource<String>>()
    val deleteUser: LiveData<Resource<String>>
        get() = _deleteUser

    fun deleteMember(familyHash : String, memberHash : String) {
        _deleteUser.value = Resource.loading()
        viewModelScope.launch {
            try {
                val res = dashboardRepo.deleteMember(familyHash,memberHash)
                _deleteUser.value = Resource.success(res.body()?.status)
            }catch (e : Exception){
                _deleteUser.value = Resource.error(e)
            }
        }
    }

    private var _getAllKarya = MutableLiveData<Resource<KaryakarniResponse>>()
    val getAllKarya: LiveData<Resource<KaryakarniResponse>>
        get() = _getAllKarya

    fun getAllKaryakarni(){
        _getAllKarya.value = Resource.loading()
        viewModelScope.launch {
            try{
                val response = dashboardRepo.getAllKaryakarni()
                if(response.isSuccessful){
                    _getAllKarya.value = Resource.success(response.body())
                }else if(response.code() == 404){
                    _getAllKarya.value = Resource.error(Exception(Constants.Error404))
                }
                else{
                    _getAllKarya.value = Resource.error( Exception(response.message()))
                }
            }catch (e : Exception){
                _getAllKarya.value = Resource.error(e)
            }
        }
    }


}
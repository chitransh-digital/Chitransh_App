package com.example.communityapp.ui.family

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.AllKaryakarni
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.newModels.addMember
import com.example.communityapp.data.newModels.addMemberReq
import com.example.communityapp.data.newModels.FamilyResponse
import com.example.communityapp.data.repository.FamilyRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(private var familyRepo: FamilyRepo) : ViewModel() {

    //add Member
    private val _user = MutableLiveData<Resource<String>>()
    val user: LiveData<Resource<String>>
        get() = _user

    fun addMember(addMember : addMemberReq, selectedImagePath : MultipartBody.Part?,
                  familyHash : String) {
        _user.value = Resource.loading()
        viewModelScope.launch {
            try{
                if(selectedImagePath != null){
                    val res = familyRepo.uploadImage(selectedImagePath)
                    if (res.isSuccessful){
                        addMember.memberData.profilePic = res.body()?.file!!
                    }else{
                        _user.value = Resource.error(Exception("Image Upload Failed"))
                        return@launch
                    }
                }

                val response = familyRepo.addMember(addMember, familyHash)
                Log.e("response", response.toString())
                if(response.isSuccessful){
                    _user.value = Resource.success(response.body()?.message)
                }else{
                    _user.value = Resource.error(Exception(response.message()))
                }
            }catch (e : Exception) {
                _user.value = Resource.error(e)
            }
        }
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

    private var _getAllKarya = MutableLiveData<Resource<AllKaryakarni>>()
    val getAllKarya: LiveData<Resource<AllKaryakarni>>
        get() = _getAllKarya

    fun getAllKaryakarni(){
        _getAllKarya.value = Resource.loading()
        viewModelScope.launch {
            try{
                val response = familyRepo.getAllKaryakarni()
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
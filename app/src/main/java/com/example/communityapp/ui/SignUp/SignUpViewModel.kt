package com.example.communityapp.ui.SignUp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.newModels.AllKaryakarni
import com.example.communityapp.data.newModels.CreateFamilyResponse
import com.example.communityapp.data.newModels.ImageResponse
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.newModels.MemberData
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.data.newModels.SignupResponse
import com.example.communityapp.data.newModels.UpdateImage
import com.example.communityapp.data.repository.SignUpRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private var signUpRepo: SignUpRepo) : ViewModel() {

    private val _user = MutableLiveData<Resource<SignupResponse>>()
    val user: LiveData<Resource<SignupResponse>>
        get() = _user

    fun addMember(signupRequest: SignupRequest, imagePart: MultipartBody.Part, context: Context) {

        _user.value = Resource.loading()
        viewModelScope.launch {
            try{
                val imageUrl = signUpRepo.uploadImage(imagePart,context)

                if(imageUrl.isSuccessful){
                    Log.d("SignUpViewModel", "addMember: ${imageUrl.body()}")
                    signupRequest.memberData.profilePic = imageUrl.body()?.file!!

                    val res = signUpRepo.addMember(signupRequest)
                    if(res.isSuccessful){
                        Log.d("SignUpViewModel", "addMember: ${res.body()}")
                        _user.value = Resource.success(res.body())
                    }else if(res.code() == 404){
                        _user.value = Resource.error(Exception(Constants.Error404))
                    }
                }
                else{
                    _user.value = Resource.error(Exception(Constants.Error404))
                }
            }catch (e : Exception){
                _user.value = Resource.error(e)
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
                val res = signUpRepo.signInWithPhone(phone)
                if(res.isSuccessful){
                    Log.d("LoginViewModel", "signInWithPhone: ${res.body()}")
                    _loginStatusPhone.value = Resource.success(res.body())
                }else if(res.code() == 404){
                    _loginStatusPhone.value = Resource.error(Exception(Constants.Error404))
                }
            }catch (e : Exception){
                _loginStatusPhone.value = Resource.error(e)
            }
        }
    }

    //createFamily
    private val _createFamily = MutableLiveData<Resource<CreateFamilyResponse>>()
    val createFamily: LiveData<Resource<CreateFamilyResponse>>
        get() = _createFamily

    fun createFamily(phone: String, familyID: String, memberData: String) {
        _createFamily.value = Resource.loading()
        viewModelScope.launch {
            try {
                val res = signUpRepo.createFamily(phone, familyID, memberData)
                if (res.isSuccessful) {
                    Log.d("SignUpViewModel", "createFamily: ${res.body()}")
                    _createFamily.value = Resource.success(res.body())
                } else if (res.code() == 404) {
                    _createFamily.value = Resource.error(Exception(Constants.Error404))
                }
            } catch (e: Exception) {
                _createFamily.value = Resource.error(e)
            }
        }
    }

    //updateMember
    private val _updateMember = MutableLiveData<Resource<SignupResponse>>()
    val updateMember: LiveData<Resource<SignupResponse>>
        get() = _updateMember

    fun updateMember(imageURl:String, familyHash: String, memberHash: String) {
        _updateMember.value = Resource.loading()
        viewModelScope.launch {
            try {
                val res = signUpRepo.updateMember(UpdateImage(MemberData(profilePic =  imageURl)), familyHash, memberHash)
                if (res.isSuccessful) {
                    _updateMember.value = Resource.success(res.body())
                } else if (res.code() == 404) {
                    _updateMember.value = Resource.error(Exception(Constants.Error404))
                }
                else {
                    _updateMember.value = Resource.error(Exception(res.message()))
                }
            } catch (e: Exception) {
                _updateMember.value = Resource.error(e)
            }
        }
    }

    //addImage
    private val _addImage = MutableLiveData<Resource<ImageResponse>>()
    val addImage: LiveData<Resource<ImageResponse>>
        get() = _addImage

    fun addImage(imagePart: MultipartBody.Part, context: Context) {
        _addImage.value = Resource.loading()
        viewModelScope.launch {
            try {
                val res = signUpRepo.uploadImage(imagePart, context)
                if (res.isSuccessful) {
                    Log.d("SignUpViewModel", "addImage: ${res.body()}")
                    _addImage.value = Resource.success(res.body())
                } else if (res.code() == 404) {
                    _addImage.value = Resource.error(Exception(Constants.Error404))
                }else{
                    _addImage.value = Resource.error(Exception(res.message()))
                }
            } catch (e: Exception) {
                _addImage.value = Resource.error(e)
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
                val response = signUpRepo.getAllKarya()
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
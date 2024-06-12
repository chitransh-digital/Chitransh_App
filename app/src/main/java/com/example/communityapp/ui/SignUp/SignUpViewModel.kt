package com.example.communityapp.ui.SignUp

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.data.newModels.SignupResponse
import com.example.communityapp.data.repository.SignUpRepo
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private var signUpRepo: SignUpRepo) : ViewModel() {

    private val _user = MutableLiveData<Resource<SignupResponse>>()
    val user: LiveData<Resource<SignupResponse>>
        get() = _user

    fun addMember(signupRequest: SignupRequest,imagePath: Uri,context: Context) {

        _user.value = Resource.loading()
        viewModelScope.launch {
            try{
                val imageUrl = signUpRepo.uploadImage(imagePath,context)

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

}
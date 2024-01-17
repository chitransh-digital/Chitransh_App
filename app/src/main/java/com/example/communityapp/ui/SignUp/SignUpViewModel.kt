package com.example.communityapp.ui.SignUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.repository.SignUpRepo
import com.example.communityapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private var signUpRepo: SignUpRepo) : ViewModel() {

    private val _user = MutableLiveData<Resource<String>>()
    val user: LiveData<Resource<String>>
        get() = _user

    fun addMember(member: Member,imagePath: String) {
        signUpRepo.addMember(member, imagePath).onEach {
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

}
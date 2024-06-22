package com.example.communityapp.data.repository

import androidx.lifecycle.ViewModel
import com.example.communityapp.data.retrofit.CustomAPI
import javax.inject.Inject

class KaryakarniRepo @Inject constructor(private val api: CustomAPI) : ViewModel(){

    suspend fun getKaryakarni(limit: Int, page: Int) = api.getKaryakarni(limit, page)

}
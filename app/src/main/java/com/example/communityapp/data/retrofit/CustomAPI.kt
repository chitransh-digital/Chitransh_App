package com.example.communityapp.data.retrofit

import com.example.communityapp.data.models.LoginRequest
import com.example.communityapp.data.models.LoginRequestByID
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.newModels.ImageResponse
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.data.newModels.SignupResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CustomAPI {

    @POST("api/user/loginPhone")
    suspend fun loginPhone(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/user/loginPhoneFamilyID")
    suspend fun loginWithFamilyID(@Body request: LoginRequestByID): Response<LoginResponse>


    @POST("api/member/addMember/")
    suspend fun addMember(@Body request: SignupRequest): Response<SignupResponse>

    @Multipart
    @POST("api/image/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageResponse>


}
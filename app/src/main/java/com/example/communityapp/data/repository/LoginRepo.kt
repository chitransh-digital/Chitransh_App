package com.example.communityapp.data.repository

import com.example.communityapp.data.models.LoginRequest
import com.example.communityapp.data.models.LoginRequestByID
import com.example.communityapp.data.newModels.SMSRequest
import com.example.communityapp.data.retrofit.CustomAPI
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LoginRepo @Inject constructor(private val auth: FirebaseFirestore, private val api: CustomAPI) {

    suspend fun signInWithUsername(familyID: String,contact :String) = api.loginWithFamilyID(
        LoginRequestByID(familyID,contact)
    )

    suspend fun signInWithPhone(phone: String) = api.loginPhone(LoginRequest(phone))

    suspend fun sendOTP(url:String,authKey: String,smsRequest: SMSRequest) = api.sendOtp(url,authKey,smsRequest)

}
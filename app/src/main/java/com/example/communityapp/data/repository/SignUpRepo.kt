package com.example.communityapp.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.communityapp.data.models.LoginRequest
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.ImageResponse
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.data.newModels.UpdateImage
import com.example.communityapp.data.retrofit.CustomAPI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignUpRepo @Inject constructor(private val db : FirebaseFirestore,private val storage: FirebaseStorage,private val api: CustomAPI){

    // Make subscribeToTopic() a suspendCoroutine function to run with uploadImage() in parallel

    private suspend fun subscribeToTopic() {
        // Call the suspend function within a coroutine
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().subscribeToTopic("notify")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val msg = "Subscribed to topic"
                        Log.d("SignUp Repo Subscribe", msg)
                        continuation.resume(Unit)
                    } else {
                        val msg = "Subscribe to topic failed: ${task.exception?.message}"
                        Log.d("SignUp Repo Subscribe", msg)
                        continuation.resumeWithException(task.exception!!)
                    }
                }

        }
    }


    suspend fun uploadImage(imagePart: MultipartBody.Part, context: Context): Response<ImageResponse> {
        return api.uploadImage(imagePart)
    }


    private fun prepareFilePart(partName: String, fileUri: Uri, context: Context): MultipartBody.Part {
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)

        val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

        val requestBody = inputStream?.use { input ->
            input.readBytes().toRequestBody(mimeType.toMediaType())
        }

        return MultipartBody.Part.createFormData(partName, "filename", requestBody!!)
    }






    private fun generateMemberId(member: Member): String {
        val inputString = "${member.name}_${member.age}_${member.familyID.hashCode()}"

        return hashString("SHA-256", inputString)
    }

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest.getInstance(type).digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }


    suspend fun signInWithPhone(phone: String) = api.loginPhone(LoginRequest(phone))

    suspend fun addMember(signupRequest: SignupRequest) = api.addMember(signupRequest)

    suspend fun createFamily(phone: String, familyID: String,memberData:String) = api.createFamily(phone, familyID,memberData)

    suspend fun updateMember(imageURl: UpdateImage, familyHash: String, memberHash: String) = api.updateMemberImage(imageURl, familyHash, memberHash)

    suspend fun getAllKarya() = api.getAllKarya()
}
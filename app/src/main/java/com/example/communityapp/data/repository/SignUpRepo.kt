package com.example.communityapp.data.repository

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SignUpRepo @Inject constructor(private val db : FirebaseFirestore,private val storage: FirebaseStorage){

    fun addMember(member: Member, selectedImagePath: String): Flow<Resource<String>> {

        return flow {
            emit(Resource.loading())
            var ch = "OK"
            try {
                // Upload image to Firebase Storage
                if(selectedImagePath.isNotEmpty()){
                    val imageUrl = uploadImage(selectedImagePath, generateMemberId(member))

                    // Associate image URL with the member
                    member.profilePic = imageUrl
                }

                subscribeToTopic()


                // Save member to Firestore
                val familyCollection = db.collection(Constants.FAMILY)
                    .document(member.familyID)
                    .collection(Constants.MEMBER)
                    .document(member.contact)
                val userCollection = db.collection(Constants.USERS)
                    .document(member.contact)

                db.runBatch { batch ->
                    batch.set(userCollection, User(member.familyID, member.contact))
                    batch.set(familyCollection, member)
                }.addOnCompleteListener {
                    ch = "OK"
                }.addOnFailureListener {
                    ch = it.message.toString()
                }
                if (ch == "OK") emit(Resource.success("OK"))
                else emit(Resource.error(java.lang.Exception(ch)))
            } catch (e: Exception) {
                emit(Resource.error(e))
            }
        }
    }

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

    private suspend fun uploadImage(imagePath: String, imageName: String): String {
        return suspendCoroutine { continuation ->
            val imageRef = storage.reference.child("images/$imageName.jpg")
            val uploadTask = imageRef.putFile(Uri.fromFile(File(imagePath)))

            uploadTask.addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    continuation.resume(uri.toString())
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    private fun generateMemberId(member: Member): String {
        val inputString = "${member.name}_${member.age}_${member.familyID.hashCode()}"

        return hashString("SHA-256", inputString)
    }

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest.getInstance(type).digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

}
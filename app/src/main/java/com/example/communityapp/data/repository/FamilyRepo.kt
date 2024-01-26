package com.example.communityapp.data.repository

import android.net.Uri
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FamilyRepo @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

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


                // Save member to Firestore
                val familyCollection = db.collection(Constants.FAMILY)
                    .document(member.familyID)
                    .collection(Constants.MEMBER)
                    .document(generateMemberId(member))
                val userCollection = db.collection(Constants.USERS)
                    .document(generateMemberId(member))

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

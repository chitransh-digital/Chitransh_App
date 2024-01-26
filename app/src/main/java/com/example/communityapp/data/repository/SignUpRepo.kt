package com.example.communityapp.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class SignUpRepo @Inject constructor(private val db : FirebaseFirestore,private val storage: FirebaseStorage){

    fun addMember(member : Member,imagePath: String) : Flow<Resource<String>> {

        return flow {
            emit(Resource.loading())
            var ch = "OK"
            try {
                val familyCollection = db.collection(Constants.FAMILY).document(member.familyID).collection(
                    Constants.MEMBER)
                    .document(member.contact)
                val userCollection = db.collection(Constants.USERS).document(member.contact)

                val imageRef = storage.reference.child("profileImages/${member.contact}")
                val uploadTask = imageRef.putFile(Uri.fromFile(File(imagePath)))

                uploadTask.await()

                if(!uploadTask.isSuccessful) throw uploadTask.exception!!

                val downloadUrl = imageRef.downloadUrl.await()
                member.profilePic = downloadUrl.toString()

                db.runBatch { batch ->
                    batch.set(userCollection, User(member.familyID,member.contact))
                    batch.set(familyCollection,member)
                }.addOnCompleteListener {
                    ch = "OK"
                }.addOnFailureListener {
                    ch = it.message.toString()
                }
                if(ch == "OK") emit(Resource.success("OK"))
                else emit(Resource.error(java.lang.Exception(ch)))
            }catch (e : Exception){
                emit(Resource.error(e))
            }
        }
    }

}
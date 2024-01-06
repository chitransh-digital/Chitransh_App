package com.example.communityapp.data.repository

import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FamilyRepo @Inject constructor(private val db : FirebaseFirestore) {


    fun addMember(member : Member) : Flow<Resource<String>> {

        return flow {
            emit(Resource.loading())
            var ch = "OK"
            try {
                val familyCollection = db.collection(Constants.FAMILY).document(member.familyID).collection(Constants.MEMBER)
                    .document(member.uuid)
                val userCollection = db.collection(Constants.USERS).document(member.uuid)

                db.runBatch { batch ->
                    batch.set(userCollection, User(member.uuid,member.familyID))
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
package com.example.communityapp.data.repository

import com.example.communityapp.data.models.Business
import com.example.communityapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BusinessRepo @Inject constructor(private val db : FirebaseFirestore) {

    suspend fun addBusiness(business : Business) {
        return suspendCoroutine {continuation ->
            val businessCollection = db.collection(Constants.BUSINESS)
            val busDoc = businessCollection.document(business.ownerID).set(business)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }.addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }
    }

}
package com.example.communityapp.data.repository

import android.util.Log
import com.example.communityapp.data.models.Member
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DashboardRepo @Inject constructor(private val db : FirebaseFirestore) {

    suspend fun findMember(contact : String) : Member{
        return suspendCoroutine {continuation ->
            db.collection(Constants.USERS).document(contact).get().addOnSuccessListener {
                db.collection(Constants.FAMILY).document(it.get("familyID").toString())
                    .collection(Constants.MEMBER).document(contact).get().addOnSuccessListener {document->
                        val name = document.data?.get("name").toString()
                        val contact = document.data?.get("contact").toString()
                        val address = document.data?.get("address").toString()
                        val familyID = document.data?.get("familyID").toString()
                        val age = document.data?.get("age").toString().toInt()
                        val gender = document.data?.get("gender").toString()
                        val karyakarni = document.data?.get("karyakarni").toString()
                        continuation.resume(Member(name = name,
                            contact = contact, address = address, familyID = familyID,
                            age = age, gender = gender, karyakarni = karyakarni))
                    }.addOnFailureListener{
                        continuation.resumeWithException(it)
                    }
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }
    
}
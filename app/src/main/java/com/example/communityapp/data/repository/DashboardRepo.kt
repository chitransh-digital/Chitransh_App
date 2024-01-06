package com.example.communityapp.data.repository

import android.util.Log
import com.example.communityapp.data.models.Member
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DashboardRepo @Inject constructor(private val db: FirebaseFirestore) {

    suspend fun findMember(contact: String): List<Member> {
        return suspendCoroutine { continuation ->
            db.collection(Constants.USERS).document(contact).get().addOnSuccessListener {
                db.collection(Constants.FAMILY).document(it.get("familyID").toString())
                    .collection(Constants.MEMBER).get().addOnSuccessListener { document ->
                        val list: ArrayList<Member> = ArrayList()

                        for (ip in document) {
                            list.add(
                                Member(
                                    name = ip.get(Constants.NAME).toString(),
                                    contact = ip.get(Constants.CONTACT).toString(),
                                    address = ip.get(Constants.ADDRESS).toString(),
                                    gender = ip.get(Constants.GENDER).toString(),
                                    age = ip.get(Constants.AGE).toString().toInt(),
                                    karyakarni = ip.get(Constants.KARYAKARNI).toString(),
                                    familyID = ip.get(Constants.familyYID).toString(),
                                    uuid = ip.get(Constants.UUID).toString(),
                                    DOB = ip.get(Constants.DOB).toString()

                                )
                            )
                        }
                        continuation.resume(list)
                    }.addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

}
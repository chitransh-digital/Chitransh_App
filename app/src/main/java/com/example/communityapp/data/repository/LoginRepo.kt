package com.example.communityapp.data.repository

import android.util.Log
import com.example.communityapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginRepo @Inject constructor(private val auth: FirebaseFirestore) {
    suspend fun signInWithUsername(username: String) : String {
        return suspendCoroutine { continuation ->
            val userCollection = auth.collection("USERS")
            val userDoc = userCollection.document(username).get()
                .addOnSuccessListener {
                    Log.d("LoginRepo", "signInWithUsername: ${it.data}")
                    val id = it.get(Constants.familyID).toString()
                    continuation.resume(id)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}
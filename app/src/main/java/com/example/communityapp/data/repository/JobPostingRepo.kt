package com.example.communityapp.data.repository

import com.example.communityapp.data.models.Job
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class JobPostingRepo @Inject constructor(private val db: FirebaseFirestore) {

    suspend fun addJob(job: Job) {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("JOBS")

            // Add the job to the "jobs" collection
            val jobDocument = jobsCollection.document()
            jobDocument.set(job)
                .addOnSuccessListener {
                    // Once the job is added successfully
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}
package com.example.communityapp.data.repository

import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.models.Job
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class JobsRepo @Inject constructor(private val db: FirebaseFirestore) {


    //add new jobs
    suspend fun addJob(job: Job) {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("jobs")

            // Add the job to the "jobs" collection
            val jobDocument = jobsCollection.document()
            jobDocument.set(job)
                .addOnSuccessListener {
                    // Once the job is added successfully, add the empty "comments" collection
                    val commentsCollection = jobDocument.collection("comments")
                    commentsCollection.add(mapOf("placeholder" to true))
                        .addOnSuccessListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    //get all the jobs
    suspend fun getAllJobs(): List<Job> {
        return suspendCoroutine { continuation ->
            db.collection("jobs")
                .get()
                .addOnSuccessListener { result ->
                    val jobsList = mutableListOf<Job>()
                    for (document in result) {
                        // Map Firestore document to your Job class
                        val job = document.toObject(Job::class.java)
                        jobsList.add(job)
                    }
                    continuation.resume(jobsList)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun deleteJobById(jobId: String) {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("jobs")

            // Delete the job document
            val jobDocument = jobsCollection.document(jobId)
            jobDocument.delete()
                .addOnSuccessListener {
                    // Once the job document is deleted, delete the "comments" sub-collection
                    val commentsCollection = jobDocument.collection("comments")
                    deleteSubcollection(commentsCollection) {
                        continuation.resume(Unit)
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    private fun deleteSubcollection(collection: CollectionReference, onComplete: () -> Unit) {
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()

                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onComplete.invoke()
                    }
            }
    }


    //add comment to a job
    suspend fun addComment(jobId: String, comment: Comment) {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("jobs")

            // Add the comment to the "comments" sub-collection
            val jobDocument = jobsCollection.document(jobId)
            val commentsCollection = jobDocument.collection("comments")
            commentsCollection.add(comment)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    //get all comments of a job
    suspend fun getAllComments(jobId: String): List<Comment> {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("jobs")

            // Get all the comments from the "comments" sub-collection
            val jobDocument = jobsCollection.document(jobId)
            val commentsCollection = jobDocument.collection("comments")
            commentsCollection.get()
                .addOnSuccessListener { result ->
                    val commentsList = mutableListOf<Comment>()
                    for (document in result) {
                        // Map Firestore document to your Comment class
                        val comment = document.toObject(Comment::class.java)
                        commentsList.add(comment)
                    }
                    continuation.resume(commentsList)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }



}
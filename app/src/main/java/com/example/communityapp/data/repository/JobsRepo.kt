package com.example.communityapp.data.repository

import android.os.Build
import android.util.Log.e
import androidx.annotation.RequiresApi
import com.example.communityapp.data.models.Comment
import com.example.communityapp.data.models.Job
import com.example.communityapp.data.retrofit.CustomAPI
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class JobsRepo @Inject constructor(private val db: FirebaseFirestore,private val api: CustomAPI) {


    //add new jobs
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

    //get all the jobs
    suspend fun getAllJobs(): List<Pair<Job,String>> {
        return suspendCoroutine { continuation ->
            db.collection("JOBS")
                .get()
                .addOnSuccessListener { result ->
                    val jobsList = mutableListOf<Pair<Job,String>>()
//                    e("JobsRepo", "getAllJobs: ${result.documents}")
                    for (document in result) {
                        val id=document.id
                        // Map Firestore document to your Job class
                        e("JobsRepo", "docList: ${document}")
                        val name = document.data.get("businessName").toString()
                        val contact = document.data.get("contact").toString()
                        val jobDescription = document.data.get("jobDescription").toString()
                        val jobTitle = document.data.get("jobTitle").toString()
                        val requirements = document.data.get("requirements") as List<String>
                        val salary = document.data.get("salary").toString().toInt()
                        val location = document.data.get("location").toString()
                        val externalLink = document.data.get("externalLink").toString()
                        val job = Job(name, contact, jobDescription, jobTitle, requirements, salary,location,externalLink)
                        jobsList.add(Pair(job,id))

//                        e("JobsRepo", "job: ${job}")
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
            val jobsCollection = db.collection("JOBS")

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
            val jobsCollection = db.collection("JOBS")

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
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllComments(jobId: String): List<Comment> {
        return suspendCoroutine { continuation ->
            val jobsCollection = db.collection("JOBS")

            // Get all the comments from the "comments" sub-collection
            val jobDocument = jobsCollection.document(jobId)
            val commentsCollection = jobDocument.collection("comments")
            commentsCollection.get()
                .addOnSuccessListener { result ->
                    val commentsList = mutableListOf<Comment>()
                    for (document in result) {
                        // Map Firestore document to your Comment class

                        val name = document.data.get("name").toString()
                        val time = document.data.get("time").toString()
                        val value = document.data.get("value").toString()
                        val comment = Comment(name, time, value)

//                        val comment = document.toObject(Comment::class.java)
                        commentsList.add(comment)
                    }
                    continuation.resume(commentsList)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    //get all the jobs from the server
    suspend fun getJobs(limit: Int, page: Int) = api.getJobs(limit, page)

}
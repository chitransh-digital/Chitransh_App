package com.example.communityapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DashboardRepo @Inject constructor(private val db: FirebaseFirestore) {

    suspend fun findMember(uuid: String): List<Member> {
        return suspendCoroutine { continuation ->
            db.collection(Constants.USERS).document(uuid).get().addOnSuccessListener {
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
                                    DOB = ip.get(Constants.DOB).toString(),
                                    relation = ip.get(Constants.RELATION).toString(),
                                    bloodGroup = ip.get(Constants.BLOOD_GROUP).toString(),
                                    occupation = ip.get(Constants.OCCUPATION).toString(),
                                    education = ip.get(Constants.EDUCATION).toString(),
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


    private fun addFeedToFirestore(feedData: NewsFeed, imageUrls: List<String>, continuation: Continuation<Unit>) {
        // Add the feed document to Firestore
        val feedsCollection = db.collection("FEEDS")
        val feedDocument = feedsCollection.document()

        val feedWithImages = feedData.copy(images = imageUrls)
        feedDocument.set(feedWithImages)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }



    //get feeds with paging
    suspend fun getFeeds(lastFeed: NewsFeed? = null, limit: Long = 10): List<NewsFeed> {
        return suspendCoroutine { continuation ->
            val feedsCollection = db.collection("FEEDS")
            var query = feedsCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(limit)

            if (lastFeed != null) {
                query = query.startAfter(lastFeed.timestamp)
            }

            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val feeds = mutableListOf<NewsFeed>()
                    for (document in querySnapshot.documents) {
                        val author = document.data?.get("author")
                        val body = document.data?.get("body")
                        val images = document.data?.get("images") as List<String>
                        val timestamp = document.data?.get("timestamp")
                        val title = document.data?.get("title")
                        val visible= document.data?.get("visible")
                        val location= document.data?.get("location")

                        val feed = NewsFeed(
                            author = author as String,
                            body = body as String,
                            images = images,
                            timestamp = timestamp as String,
                            title = title as String,
                            visible = visible as Boolean,
                            location = location as String
                        )
                        feed?.let {
                            feeds.add(it)
                        }
                    }

                    continuation.resume(feeds)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }


}
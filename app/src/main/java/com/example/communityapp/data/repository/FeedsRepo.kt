package com.example.communityapp.data.repository

import android.net.Uri
import com.example.communityapp.data.models.NewsFeed
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FeedsRepo @Inject constructor(private val db: FirebaseFirestore, private val storage: FirebaseStorage) {

    suspend fun addFeed(feedData: NewsFeed, imagePaths: List<String>) {
        return suspendCoroutine { continuation ->
            // Upload images to Firebase Storage
            val imageUrls = mutableListOf<String>()
            val storageRef = storage.reference.child("feed_images")

            val uploadTasks = mutableListOf<StorageTask<UploadTask.TaskSnapshot>>()
            for (index in imagePaths.indices) {
                val imagePath = imagePaths[index]
                val imageRef = storageRef.child("image_${imagePath.substringAfterLast("/")}.png")

                val uploadTask = imageRef.putFile(Uri.fromFile(File(imagePath)))

                uploadTasks.add(uploadTask)
            }

            Tasks.whenAllSuccess<UploadTask.TaskSnapshot>(uploadTasks)
                .addOnSuccessListener { taskSnapshots ->
                    for (snapshot in taskSnapshots) {
                        // Get download URL for each uploaded image
                        snapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                            imageUrls.add(uri.toString())

                            // Check if all images are uploaded
                            if (imageUrls.size == imagePaths.size) {
                                // All images uploaded, now add the feed document to Firestore
                                addFeedToFirestore(feedData, imageUrls, continuation)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
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

                        val feed = NewsFeed(
                            author = author as String,
                            body = body as String,
                            images = images,
                            timestamp = timestamp as String,
                            title = title as String,
                            visible = visible as Boolean
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
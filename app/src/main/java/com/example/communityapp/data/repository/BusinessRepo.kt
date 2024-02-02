package com.example.communityapp.data.repository

import android.net.Uri
import com.example.communityapp.data.models.Business
import com.example.communityapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BusinessRepo @Inject constructor(private val db : FirebaseFirestore,private val storage: FirebaseStorage) {

    suspend fun addBusiness(business: Business, imagesList: MutableList<String>) {
        return suspendCoroutine { continuation ->
            val businessCollection = db.collection(Constants.BUSINESS)
            val busDoc = businessCollection.document(business.ownerID).set(business)
                .addOnSuccessListener {
                    // If business data is added successfully, upload images
                    uploadImages(imagesList) { imageUrls ->
                        // Once all images are uploaded, add image URLs to the business data
                        val updatedBusiness = business.copy(images = imageUrls)
                        // Update business data in Firestore with image URLs
                        businessCollection.document(business.ownerID).set(updatedBusiness)
                            .addOnSuccessListener {
                                continuation.resume(Unit)
                            }.addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    private fun uploadImages(imagesList: List<String>, onComplete: (List<String>) -> Unit) {
        val imageUrls = mutableListOf<String>()
        // Counter to keep track of uploaded images
        var uploadedCount = 0

        for (imageUri in imagesList) {
            val imageRef = storage.reference.child(
                "business_images/${System.currentTimeMillis()}_${
                    imageUri.substringAfterLast("/")
                }"
            )
            val uploadTask = imageRef.putFile(Uri.parse(imageUri))

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Add download URL to the list
                    imageUrls.add(downloadUri.toString())
                    uploadedCount++
                    // Check if all images are uploaded
                    if (uploadedCount == imagesList.size) {
                        // Call onComplete callback with image URLs
                        onComplete(imageUrls)
                    }
                }
            }
        }
    }

}
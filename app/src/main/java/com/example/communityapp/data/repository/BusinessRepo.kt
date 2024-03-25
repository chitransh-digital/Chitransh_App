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

    suspend fun getBusiness() : List<Business>{
        return suspendCoroutine { continuation ->
            val businessCollection = db.collection(Constants.BUSINESS)
            businessCollection.get()
                .addOnSuccessListener { documents ->
                    val businessList = mutableListOf<Business>()
                    for (document in documents) {

                        businessList.add(
                            Business(
                                name = document.get(Constants.NAME).toString(),
                                desc = document.get(Constants.DESC).toString(),
                                address = document.get(Constants.ADDRESS).toString(),
                                contact = document.get(Constants.CONTACT).toString(),
                                ownerID = document.get(Constants.OWNER_ID).toString(),
                                images = document.get(Constants.IMAGES) as List<String>,
                                link = document.get(Constants.LINK).toString(),
                                type = document.get(Constants.TYPE).toString(),
                                coupon = document.get(Constants.COUPON).toString(),
                                file = document.get(Constants.FILEURL).toString()
                            )
                        )
                    }
                    continuation.resume(businessList)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun addBusiness(business: Business, fileUri: String, imagesList: MutableList<String>) {
        return suspendCoroutine { continuation ->
            val businessCollection = db.collection(Constants.BUSINESS)
            val batch = db.batch()

            // Add business data to the batch
            val businessDocRef = businessCollection.document(business.ownerID)
            batch.set(businessDocRef, business)

            // Upload images
            val imageUrls = mutableListOf<String>()
            uploadImages(imagesList) { uploadedImageUrls ->
                imageUrls.addAll(uploadedImageUrls)

                // Upload file if provided
                if (fileUri != "NA") {
                    uploadFile(fileUri) { fileUrl ->
                        // Once file is uploaded, add file URL to the business data
                        val updatedBusiness = business.copy(images = imageUrls, file = fileUrl)
                        // Update business data in Firestore with image URLs and file URL
                        batch.set(businessDocRef, updatedBusiness, com.google.firebase.firestore.SetOptions.merge())
                        // Commit the batch
                        batch.commit().addOnSuccessListener {
                            continuation.resume(Unit)
                        }.addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                    }
                } else {
                    // If no file provided, proceed with only image URLs
                    val updatedBusiness = business.copy(images = imageUrls)
                    // Update business data in Firestore with image URLs
                    batch.set(businessDocRef, updatedBusiness, com.google.firebase.firestore.SetOptions.merge())
                    // Commit the batch
                    batch.commit().addOnSuccessListener {
                        continuation.resume(Unit)
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                }
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

    private fun uploadFile(filePath: String, onComplete: (String) -> Unit) {
        val fileUri = Uri.parse(filePath)
        val fileRef = storage.reference.child("business_files/${System.currentTimeMillis()}_${fileUri.lastPathSegment}")
        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Call onComplete callback with file URL
                onComplete(downloadUri.toString())
            }
        }
    }


}
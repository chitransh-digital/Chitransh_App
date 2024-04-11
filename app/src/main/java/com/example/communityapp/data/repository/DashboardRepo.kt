package com.example.communityapp.data.repository

import android.net.Uri
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DashboardRepo @Inject constructor(private val db: FirebaseFirestore, private val storage: FirebaseStorage) {

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
                                    familyID = ip.get(Constants.familyID).toString(),
                                    DOB = ip.get(Constants.DOB).toString(),
                                    relation = ip.get(Constants.RELATION).toString(),
                                    bloodGroup = ip.get(Constants.BLOOD_GROUP).toString(),
                                    occupation = ip.get(Constants.OCCUPATION).toString(),
                                    highestEducation = ip.get(Constants.EDUCATION).toString(),
                                    branch = ip.get(Constants.BRANCH).toString(),
                                    institute = ip.get(Constants.INSTITUTE).toString(),
                                    additionalDetails = ip.get(Constants.ADDITIONAL_DETAILS).toString(),
                                    employer = ip.get(Constants.EMPLOYER).toString(),
                                    post = ip.get(Constants.POST).toString(),
                                    department = ip.get(Constants.DEPARTMENT).toString(),
                                    location = ip.get(Constants.LOCATION).toString(),
                                    profilePic = ip.get(Constants.ProfilePic).toString()
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
                            timestamp = "1985‑09‑25 17:45:30.005",
                            title = title as String,
                            visible = visible as Boolean,
                            location = location as String
                        )
                        feed.let {
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

    fun updateMember(memberId: String, updatedMember: Member, selectedImagePath: String?, change : Boolean): Flow<Resource<String>> {
        return flow {
            emit(Resource.loading())
            var ch = "OK"
            try {
                // Check if a new image is selected for update
                if (!selectedImagePath.isNullOrEmpty() && change) {
                    // Upload new image to Firebase Storage
                    val imageUrl = uploadImage(selectedImagePath, generateMemberId(updatedMember))
                    // Associate new image URL with the member
                    updatedMember.profilePic = imageUrl
                }

                // Update member in Firestore
                val familyCollection = db.collection(Constants.FAMILY)
                    .document(updatedMember.familyID)
                    .collection(Constants.MEMBER)
                    .document(memberId)

                db.runBatch { batch ->
                    batch.set(familyCollection, updatedMember)
                    // You can update other fields of the member here if needed
                }.addOnCompleteListener {
                    ch = "OK"
                }.addOnFailureListener {
                    ch = it.message.toString()
                }

                if (ch == "OK") emit(Resource.success("OK"))
                else emit(Resource.error(Exception(ch)))
            } catch (e: Exception) {
                emit(Resource.error(e))
            }
        }
    }


    private suspend fun uploadImage(imagePath: String, imageName: String): String {
        return suspendCoroutine { continuation ->
            val imageRef = storage.reference.child("images/$imageName.jpg")
            val uploadTask = imageRef.putFile(Uri.fromFile(File(imagePath)))

            uploadTask.addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    continuation.resume(uri.toString())
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    private fun generateMemberId(member: Member): String {
        val inputString = "${member.name}_${member.age}_${member.familyID.hashCode()}"

        return hashString("SHA-256", inputString)
    }

    private fun hashString(type: String, input: String): String {
        val bytes = MessageDigest.getInstance(type).digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun deleteMember(familyID : String, contact : String){
        return suspendCoroutine {continuation ->
            db.collection(Constants.FAMILY).document(familyID).
                    collection(Constants.MEMBER).document(contact).
                    delete().addOnSuccessListener {
                        continuation.resume(Unit)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }

        }
    }



}
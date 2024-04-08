package com.example.communityapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FamilyRepo @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    val hash = emptySet<String>()

    fun addMember(member: Member, selectedImagePath: String): Flow<Resource<String>> {

        return flow {
            emit(Resource.loading())
            var ch = "OK"
            try {
                // Upload image to Firebase Storage
                if (selectedImagePath.isNotEmpty()) {
                    val imageUrl = uploadImage(selectedImagePath, generateMemberId(member))

                    // Associate image URL with the member
                    member.profilePic = imageUrl
                }


                // Save member to Firestore
                val familyCollection = db.collection(Constants.FAMILY)
                    .document(member.familyID)
                    .collection(Constants.MEMBER)
                    .document(generateMemberId(member))
                val userCollection = db.collection(Constants.USERS)
                    .document(generateMemberId(member))

                db.runBatch { batch ->
                    batch.set(userCollection, User(member.familyID, member.contact))
                    batch.set(familyCollection, member)
                }.addOnCompleteListener {
                    ch = "OK"
                }.addOnFailureListener {
                    ch = it.message.toString()
                }
                if (ch == "OK") emit(Resource.success("OK"))
                else emit(Resource.error(java.lang.Exception(ch)))
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


    suspend fun findMembers(familyID: String): List<Member> {
        return suspendCoroutine { continuation ->
            db.collection(Constants.FAMILY).document(familyID)
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
                                profilePic = ip.get(Constants.ProfilePic).toString()
                            )
                        )
                    }

                    continuation.resume(list)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAllFamily() : List<List<Member>> {
        return suspendCoroutine { continuation ->
            db.collection(Constants.USERS)
                .get()
                .addOnSuccessListener { docs ->
                    var lis = mutableListOf<String>()
                    for(doc in docs){
                        lis.add(
                            doc.get(Constants.familyID).toString()
                        )
                    }

                    val list = lis.distinct()
                    Log.e("FamilyRepo", "getAllFamilySET: $list")
                    CoroutineScope(Dispatchers.Default).launch {
                        val deferredResults = list.map { doc ->
                            async {
                                getFamilyByCity(doc)
                            }
                        }

                        val familyLists = deferredResults.awaitAll()
                        for (list in familyLists) {
                            Log.d("FamilyRepo", "getAllFamily: $list")
                        }
                        continuation.resume(familyLists)
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    private suspend fun getFamilyByCity(user: String): List<Member> {
        return suspendCoroutine { continuation ->
            db.collection(Constants.FAMILY).document(user)
                .collection(Constants.MEMBER)
                .get()
                .addOnSuccessListener { document ->
                    val members = mutableListOf<Member>()

                    for (doc in document) {
                        members.add(
                            Member(
                                name = doc.get(Constants.NAME).toString(),
                                contact = doc.get(Constants.CONTACT).toString(),
                                address = doc.get(Constants.ADDRESS).toString(),
                                gender = doc.get(Constants.GENDER).toString(),
                                age = doc.get(Constants.AGE).toString().toInt(),
                                karyakarni = doc.get(Constants.KARYAKARNI).toString(),
                                familyID = doc.get(Constants.FAMILYID).toString(),
                                DOB = doc.get(Constants.DOB).toString(),
                                relation = doc.get(Constants.RELATION).toString(),
                                bloodGroup = doc.get(Constants.BLOOD_GROUP).toString(),
                                occupation = doc.get(Constants.OCCUPATION).toString(),
                                highestEducation = doc.get(Constants.EDUCATION).toString(),
                                profilePic = doc.get(Constants.ProfilePic).toString()
                            )
                        )
                    }

                    Log.e("FamilyRepo", "getFamilyByCity: $members  $user")

                    continuation.resume(members)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}
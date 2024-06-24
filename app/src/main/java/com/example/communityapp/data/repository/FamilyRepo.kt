package com.example.communityapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.User
import com.example.communityapp.data.newModels.addMember
import com.example.communityapp.data.newModels.addMemberReq
import com.example.communityapp.data.retrofit.CustomAPI
import com.example.communityapp.data.retrofit.CustomAPI
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FamilyRepo @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val api: CustomAPI
) {

    suspend fun uploadImage(imagePath : MultipartBody.Part) = api.uploadImage(imagePath)

    suspend fun getAllKaryakarni() = api.getAllKaryakarni()

    suspend fun addMember(addMember : addMemberReq, familyHash : String) = api.addMember(addMember, familyHash)

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

    suspend fun getAllFamilies(limit:Int,page:Int) = api.getAllFamilies(limit,page)
}
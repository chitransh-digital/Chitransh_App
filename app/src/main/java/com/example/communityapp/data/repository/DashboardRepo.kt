package com.example.communityapp.data.repository

import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepo @Inject constructor(private val db : FirebaseFirestore) {

    fun findMember(contact : String) : Flow<Resource<String>> {
        return flow{
            val userCollection = db.collection(Constants.USERS).document(contact)
            val familyCollection = db.collection(Constants.FAMILY)

            db.runTransaction {transaction->
                val id = transaction.get(userCollection).get(Constants.FAMILYID).toString()
                val result = transaction.get(familyCollection.document(id)
                    .collection(Constants.MEMBER).document(contact))
            }
        }
    }

}
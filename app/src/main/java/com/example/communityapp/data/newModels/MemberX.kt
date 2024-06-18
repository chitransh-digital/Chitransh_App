package com.example.communityapp.data.newModels

import java.io.Serializable

data class MemberX(
    var _id: String,
    var age: Int,
    var bloodGroup: String,
    var city: String,
    var contact: String,
    var contactVisibility: Boolean,
    var education: String,
    var educationDetails: List<Any>,
    var gender: String,
    var karyakarni: String,
    var landmark: String,
    var name: String,
    var occupation: String,
    var occupationDetails: List<Any>,
    var profilePic: String,
    var relation: String,
    var state: String
):Serializable
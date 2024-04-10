package com.example.communityapp.data.models

import java.io.Serializable

data class Member(
    val name : String,
    val age : Int,
    val address : String,
    val gender : String,
    val karyakarni : String,
    val familyID : String,
    val contact : String="",
    val DOB : String = "NA",
    var profilePic : String = "",
    val relation : String,
    val bloodGroup:String,
    val highestEducation : String,
    val branch : String = "NA",
    val institute : String = "NA",
    val additionalDetails : String = "NA",
    val occupation : String,
    val employer : String = "NA",
    val post : String = "NA",
    val department : String = "NA",
    val location : String = "NA",
    val buisType : String = "NA",
    val buisName : String = "NA",
    val course : String = "NA"
) : Serializable
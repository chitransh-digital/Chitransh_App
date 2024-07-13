package com.example.communityapp.utils

object Constants {

    const val CUSTOM_BASE_URL = "http://159.89.165.67/"
    const val OTP_URL="http://msg.msgclub.net/rest/services/sendSMS/sendGroupSms"

    const val Error404= "user not found"
    const val Error400= "Bad Request"
    const val KEY_OTP_TOKEN = "DEFAULT_OTP_KEY"
    const val FILEURL= "file"
    const val COUPON = "coupon"
    const val EDUCATION= "highestEducation"
    const val OCCUPATION= "occupation"
    const val BLOOD_GROUP= "bloodGroup"
    var token = ""
    var verID = ""

    //Shared Preferences
    const val LOGIN_FILE = "loginPrefFile"
    const val PHONE_NUMBER = "phoneNumber"

    //Collections
    const val FAMILY = "FAMILY"
    const val MEMBER = "MEMBER"
    const val USERS = "USERS"
    const val FAMILYID = "FAMILYID"
    const val BUSINESS = "BUSINESS"
    const val FEEDS = "FEEDS"

    //Fields for Member/Users
    const val UNIQUE_RELATIONS= "uniqueRelations"
    const val NAME = "name"
    const val ADDRESS = "address"
    const val CONTACT = "contact"
    const val AGE = "age"
    const val GENDER = "gender"
    const val KARYAKARNI = "karyakarni"
    const val familyID = "familyID"
    const val ProfilePic = "profilePic"
    const val DOB = "dob"
    const val RELATION = "relation"
    const val DESC = "desc"
    const val OWNER_ID = "ownerID"
    const val IMAGES = "images"
    const val LINK = "link"
    const val TYPE = "type"
    const val BRANCH = "branch"
    const val INSTITUTE = "institute"
    const val ADDITIONAL_DETAILS = "additionalDetails"
    const val EMPLOYER = "employer"
    const val POST = "post"
    const val DEPARTMENT = "department"
    const val LOCATION = "location"
    const val BUIS_TYPE = "buisType"
    const val BUIS_NAME = "buisName"
    const val COURSE = "course"

    //For data transfer b/w intents
    const val USER_DATA = "user_data"
    const val PHONE_NUM = "phone_num"
    const val HOME_FRAG = "home_frag"
    const val USERNAME = "username"
    const val FAMILYDATA = "family_data"
    const val HEAD_ADDRESS = "headAddress"
    const val FAMILYHASH = "familyHash"
    const val HEAGGENDER = "headGender"
    // Course maps
    val POSTGRADUATE_COURSE_MAP = mapOf(
        "MTech" to 0,
        "MSc" to 1,
        "MCom" to 2,
        "MA" to 3,
        "MBA" to 4,
        "MCA" to 5,
        "MPharma" to 6,
        "MDS" to 7,
        "LLM" to 8,
        "MA/LLM" to 9,
        "MCom/LLM" to 10,
        "MPharma" to 11,
        "MDS" to 12
    )

    val UNDERGRADUATE_COURSE_MAP = mapOf(
        "BTech" to 0,
        "BSc" to 1,
        "BCom" to 2,
        "BA" to 3,
        "BBA" to 4,
        "BCA" to 5,
        "BEd" to 6,
        "BPharma" to 7,
        "BDS" to 8,
        "BAMS" to 9,
        "BHMS" to 10,
        "LLB" to 11,
        "BHM" to 12,
        "BHMCT" to 13,
        "Ded" to 14,
        "BA/LLB" to 16,
        "BCom/LLB" to 17,
        "CS" to 20
    )

    val BUSINESS_TYPE_MAP = mapOf(
        "Restaurant" to 0,
        "Retail Store" to 1,
        "Tech" to 2,
        "Consulting Firm" to 3
    )
}
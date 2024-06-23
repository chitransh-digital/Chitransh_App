package com.example.communityapp.ui.Dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.PreferencesHelper
import com.example.communityapp.data.newModels.EducationDetails
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.newModels.MemberX
import com.example.communityapp.data.newModels.OccupationDetails
import com.example.communityapp.data.newModels.addMember
import com.example.communityapp.databinding.ActivityUpdateMemberBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.KaryakarniSpinnerAdapter
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class UpdateMemberActivity : BaseActivity() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var binding: ActivityUpdateMemberBinding
    private lateinit var member: MemberX
    private lateinit var viewModel: DashboardViewModel
    private var selectedImageMultiPartBody: MultipartBody.Part? = null
    private var shortAnimationDuration = 500
    private var familyMember = "other"
    private var screenPointer = 0
    private var familyHash = ""
    var uri: Uri? = null
    var eduSpinner = 0
    var occuSpinner = 0
    var ageSpinner = 0
    var buisTypeSpinner = 0
    var courseSpinner = 0
    var course = "NA"
    var buisType = "NA"
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        if (intent.hasExtra("member")) {
            member = intent.getSerializableExtra("member") as MemberX
            familyHash = intent.getStringExtra("familyHash").toString()
            familyMember = member.relation
        }
        setObservables()
        setWindowsUp()
        inito()
        viewModel.getAllKaryakarni()

        if (familyMember == "HEAD") {
            binding.previewDelete.visibility = View.GONE
        }

        populateUI()
        changeUI(screenPointer)

        binding.previewInfoNotSubmit.setOnClickListener {
            Log.d("updateMemberActivity", "preview info tapped $screenPointer")
            screenPointer++
            changeUI(screenPointer)
        }

        binding.familyBack.setOnClickListener {
            onBackPressed()
        }

        binding.eduLevelSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    eduSpinner = position
                    if (binding.eduLevelSpinner.selectedItem.toString() == "High School" ||
                        binding.eduLevelSpinner.selectedItem.toString() == "Higher Secondary School" ||
                        binding.eduLevelSpinner.selectedItem.toString() == "Junior School" ||
                        binding.eduLevelSpinner.selectedItem.toString() == "Diploma"
                    ) {
                        binding.eduDepartment.visibility = View.GONE
                        binding.eduInstitute.visibility = View.GONE
                        binding.eduAdditionalDetails.visibility = View.GONE
                        binding.eduCourse.visibility = View.GONE
                    } else {
                        binding.eduDepartment.visibility = View.GONE
                        binding.eduInstitute.visibility = View.VISIBLE
                        binding.eduAdditionalDetails.visibility = View.VISIBLE
                        binding.eduCourse.visibility = View.VISIBLE

                        if (binding.eduLevelSpinner.selectedItem.toString() == "Bachelors") {
                            val list = arrayListOf(
                                "BTech",
                                "BSc",
                                "BCom",
                                "BA",
                                "BBA",
                                "BCA",
                                "BEd",
                                "BPharma",
                                "BDS",
                                "BAMS",
                                "BHMS",
                                "LLB",
                                "BHM",
                                "BHMCT",
                                "Ded",
                                "LLB",
                                "BA/LLB",
                                "BCom/LLB",
                                "BPharma",
                                "BDS",
                                "CS",
                                "other"
                            )
                            val courseAdapter = ArrayAdapter(
                                this@UpdateMemberActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                list
                            )
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter

                            member.educationDetails?.let { educationDetails ->
                                if (educationDetails.toString().isNotEmpty()) {
                                    val course = educationDetails.course

                                    val position = Constants.UNDERGRADUATE_COURSE_MAP[course] ?: 21 // 21 is the position for "Other"
                                    binding.eduCourseSpinner.setSelection(position)

                                    if (position == 21) {
                                        binding.eduCourseOtherInput.setText(course)
                                    }
                                }
                            }


                        } else if (binding.eduLevelSpinner.selectedItem.toString() == "Masters") {
                            val list = arrayListOf(
                                "MTech",
                                "MSc",
                                "MCom",
                                "MA",
                                "MBA",
                                "MCA",
                                "MPharma",
                                "MDS",
                                "LLM",
                                "MA/LLM",
                                "MCom/LLM",
                                "MPharma",
                                "MDS",
                                "other"
                            )
                            val courseAdapter = ArrayAdapter(
                                this@UpdateMemberActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                list
                            )
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter

                            member.educationDetails?.let { educationDetails ->
                                if (educationDetails.toString().isNotEmpty()) {
                                    val jsonObject = JSONObject(educationDetails.course)
                                    val course = jsonObject.getString("course")

                                    val position = Constants.POSTGRADUATE_COURSE_MAP[course] ?: 13
                                    binding.eduCourseSpinner.setSelection(position)

                                    if (position == 13) {
                                        binding.eduCourseOtherInput.setText(course)
                                    }
                                }
                            }

                        } else if (binding.eduLevelSpinner.selectedItem.toString() == "Phd") {
                            binding.eduDepartment.visibility = View.VISIBLE
                            binding.eduInstitute.visibility = View.VISIBLE
                            binding.eduAdditionalDetails.visibility = View.VISIBLE
                            binding.eduCourse.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Optional: Handle case when nothing is selected
                }
            }


        binding.occuLevelSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    occuSpinner = position

                    if (binding.occuLevelSpinner.selectedItem.toString() == "Not Working" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "Retired" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "HouseWife" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "Student"
                    ) {
                        binding.occuDepartment.visibility = View.GONE
                        binding.occuEmployer.visibility = View.GONE
                        binding.occuPosition.visibility = View.GONE
                        binding.occuAddress.visibility = View.GONE
                        binding.occuBuisName.visibility = View.GONE
                        binding.occuBuisType.visibility = View.GONE
                    } else if (binding.occuLevelSpinner.selectedItem.toString() == "Business") {
                        binding.occuEmployer.visibility = View.GONE
                        binding.occuDepartment.visibility = View.GONE
                        binding.occuPosition.visibility = View.GONE
                        binding.occuAddress.visibility = View.VISIBLE
                        binding.occuBuisName.visibility = View.VISIBLE
                        binding.occuBuisType.visibility = View.VISIBLE
                        binding.occuAddressInput.hint = "Business Address"
                        binding.occuAddressText.text = "Business Address"
                        val businessTypeList = arrayListOf(
                            "Restaurant",
                            "Retail Store",
                            "Tech",
                            "Consulting Firm",
                            "other"
                        )
                        val businessTypeAdapter = ArrayAdapter(
                            this@UpdateMemberActivity,
                            android.R.layout.simple_spinner_dropdown_item, businessTypeList
                        )
                        businessTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.occuBuisTypeSpinner.adapter = businessTypeAdapter

                        member.occupationDetails?.let { occupationDetails ->
                            occupationDetails.businessType?.let { businessTypeString ->
                                if (businessTypeString.isNotEmpty()) {
                                    val businessType = businessTypeString

                                    val position = Constants.BUSINESS_TYPE_MAP[businessType] ?: 4 // 4 is the default position for "Other"
                                    binding.occuBuisTypeSpinner.setSelection(position)
                                }
                            }
                        }
                    } else {
                        binding.occuDepartment.visibility = View.VISIBLE
                        binding.occuEmployer.visibility = View.VISIBLE
                        binding.occuPosition.visibility = View.VISIBLE
                        binding.occuAddress.visibility = View.VISIBLE
                        binding.occuBuisName.visibility = View.GONE
                        binding.occuBuisType.visibility = View.GONE
                        binding.occuAddressInput.hint = "Job Location"
                        binding.occuAddressText.text = "Job Location"
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Optional: Handle case when nothing is selected
                }
            }

        binding.ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                ageSpinner = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle case when nothing is selected
            }
        }


        binding.occuBuisTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    buisTypeSpinner = position
                    buisType = binding.occuBuisTypeSpinner.selectedItem.toString()

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Optional: Handle case when nothing is selected
                }
            }

        binding.eduCourseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    courseSpinner = position

                    if (binding.eduCourseSpinner.selectedItem.toString() == "other") {
                        binding.eduCourseOtherInput.visibility = View.VISIBLE
                    } else {
                        binding.eduCourseOtherInput.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Optional: Handle case when nothing is selected
                }
            }

        binding.previewDelete.setOnClickListener {
            showDeleteMemberDialog(this, member)
        }

    }

    private fun registerPageUI() {

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male", "Female")
        val genadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

        if (familyMember == "Father") {
            binding.genderSpinner.setSelection(0)
        } else if (familyMember == "Mother") {
            binding.genderSpinner.setSelection(1)
        } else if (familyMember == "Son") {
            binding.genderSpinner.setSelection(0)
        } else if (familyMember == "Daughter") {
            binding.genderSpinner.setSelection(1)
        } else if (familyMember == "Husband") {
            binding.genderSpinner.setSelection(0)
        } else if (familyMember == "Wife") {
            binding.genderSpinner.setSelection(1)
        } else {
            binding.genderSpinner.setSelection(0)
        }

        if (familyMember == "other") {
            binding.relationSpinner.visibility = View.VISIBLE
            binding.relationinput.visibility = View.GONE
        } else {
            binding.relationSpinner.visibility = View.GONE
            binding.relationinput.visibility = View.VISIBLE
        }

        val occupationList = arrayListOf(
            "Student",
            "Government Job",
            "Private Job",
            "Retired",
            "Business",
            "Doctor",
            "Lawyer",
            "Chartered Accountant",
            "Not Working"
        )
        if (familyMember == "Mother" || familyMember == "Wife" || familyMember == "Daughter") {
            occupationList.add("HouseWife")
        }

        val occupationadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, occupationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.occuLevelSpinner.adapter = occupationadapter

        val educationList = arrayListOf(
            "Junior School",
            "High School",
            "Higher Secondary School",
            "Diploma",
            "Bachelors",
            "Masters",
            "Phd"
        )
        val educationadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, educationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eduLevelSpinner.adapter = educationadapter

        val state = member.state
        val city = member.city
        val landmark = member.landmark
        Log.e("State", state)
        Log.e("City", city)
        Log.e("Landmark", landmark)

        init(city, state)

        //add blood groups in blood group spinners
        val bloodGroupList = arrayListOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "other")
        val bloodGroupadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroupList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupadapter

        val relationList = arrayListOf("Brother", "Sister", "GrandMother", "GrandFather")
        val relationadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, relationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.relationSpinner.adapter = relationadapter

        val no = preferencesHelper.getContact()

        binding.relationinput.setText(familyMember)

        Log.d("Dashboard phone no", no.toString())

        binding.ageSpinner.setSelection(member.age - 1)
        var gender = 0
        if (member.gender != "Male") {
            gender = 1
        }

        binding.genderSpinner.setSelection(
            gender
        )

        if (member.bloodGroup == "A+") {
            binding.bloodGroupSpinner.setSelection(0)
        } else if (member.bloodGroup == "A-") {
            binding.bloodGroupSpinner.setSelection(1)
        } else if (member.bloodGroup == "B+") {
            binding.bloodGroupSpinner.setSelection(2)
        } else if (member.bloodGroup == "B-") {
            binding.bloodGroupSpinner.setSelection(3)
        } else if (member.bloodGroup == "AB+") {
            binding.bloodGroupSpinner.setSelection(4)
        } else if (member.bloodGroup == "AB-") {
            binding.bloodGroupSpinner.setSelection(5)
        } else if (member.bloodGroup == "O+") {
            binding.bloodGroupSpinner.setSelection(6)
        } else if (member.bloodGroup == "O-") {
            binding.bloodGroupSpinner.setSelection(7)
        } else {
            binding.bloodGroupSpinner.setSelection(8)
        }

        binding.memberSubmit.setOnClickListener {
            checkDetails1()
        }

        binding.familyBack.setOnClickListener {
            onBackPressed()
        }

        binding.addImageText.setOnClickListener {
            openFilePicker()
        }

        binding.occuBuisSubmit.setOnClickListener {
            checkDetails2()
        }

        binding.familyRegistrationUpdate.setOnClickListener {
            submitRegistration()
        }

        binding.imagePrevious.setOnClickListener {
            screenPointer--
            changeUI(screenPointer)
        }

        binding.buisOccuPrevious.setOnClickListener {
            screenPointer--
            changeUI(screenPointer)
        }


    }

    private fun submitRegistration() {
        val completeAddress =
            binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()


        var contact = "NA"
        if (binding.contactinput.text.toString() != "+91") {
            contact = binding.contactinput.text.toString()
        }
        var education = binding.eduLevelSpinner.selectedItem.toString()
        if (binding.eduInstituteInput.text.isNotEmpty()) {
            education += "," + binding.eduInstituteInput.text.toString()
        }

        var karyakanri = "NA"
        if (binding.KaryainputSpinner.isSelected) {
            karyakanri = binding.KaryainputSpinner.selectedItem.toString()
        }

        val updatedMember = addMember(
            memberData = MemberX(
                _id = member._id,
                name = binding.nameinput.text.toString(),
                familyID = binding.IDinput.text.toString(),
                age = binding.ageSpinner.selectedItem.toString().toInt(),
                gender = binding.genderSpinner.selectedItem.toString(),
                bloodGroup = binding.bloodGroupSpinner.selectedItem.toString(),
                city = binding.citySpinner.selectedItem.toString(),
                landmark = binding.landmarkInput.text.toString(),
                state = binding.stateSpinner.selectedItem.toString(),
                contact = contact,
                contactVisibility = true,
                karyakarni = karyakanri,
                relation = binding.relationinput.text.toString(),
                education = education,
                educationDetails = EducationDetails(
                    course = course,
                    fieldOfStudy = binding.eduDepartInput.text.toString(),
                    institute = binding.eduInstituteInput.text.toString(),
                    additionalDetails = binding.eduAdditionalInput.text.toString()
                ),
                occupation = binding.occuLevelSpinner.selectedItem.toString(),
                occupationDetails = OccupationDetails(
                    businessType = buisType,
                    businessName = if (binding.occuBuisNameInput.text.isNotEmpty()) binding.occuBuisNameInput.text.toString() else "NA",
                    jobEmployer = if (binding.occuEmployerInput.text.isNotEmpty()) binding.occuEmployerInput.text.toString() else "NA",
                    jobDepartment = if (binding.occuDepartmentInput.text.isNotEmpty()) binding.occuDepartmentInput.text.toString() else "NA",
                    jobLocation = if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA",
                    jobPost = if (binding.occuPositioninput.text.isNotEmpty()) binding.occuPositioninput.text.toString() else "NA",
                    businessAddress = if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA"
                ),
                profilePic = member.profilePic
            )
        )

        Log.d("UpdateMemberActivity", "Updated Member $updatedMember")
        viewModel.updateMember(updatedMember, selectedImageMultiPartBody,familyHash)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
//        val pattern = """^\+91\d{10}$""".toRegex()
//        if (phoneNumber == "+91") {
//            return true
//        }
//
//        val matchResult = pattern.find(phoneNumber)
//
//        return matchResult != null

        return phoneNumber.length == 10
    }

    private fun checkDetails1() {
        if (binding.nameinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else if (!isValidPhoneNumber(binding.contactinput.text.toString())) {
            Toast.makeText(this, "Please enter valid contact no", Toast.LENGTH_SHORT).show()
        } else if (binding.ageSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
        } else if (binding.genderSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        } else if (binding.relationinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your relation", Toast.LENGTH_SHORT).show()
        } else if (binding.IDinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your FamilyID", Toast.LENGTH_SHORT).show()
        } else if (binding.bloodGroupSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Blood Group", Toast.LENGTH_SHORT).show()
        } else {
            binding.nameinput.setText(capitalizeNames(binding.nameinput.text.toString()))
            screenPointer++
            changeUI(screenPointer)
        }
    }

    private fun capitalizeNames(input: String): String {
        val words = input.split(" ")
        val capitalizedWords = words.map { it.toLowerCase().capitalize() }
        return capitalizedWords.joinToString(" ")
    }

    private fun checkDetails2() {
        if (binding.occuLevelSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your occupation", Toast.LENGTH_SHORT).show()
        } else if (binding.eduLevelSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your education", Toast.LENGTH_SHORT).show()
        } else {
            screenPointer++
            changeUI(screenPointer)
        }

        course =
            if (binding.eduCourseSpinner.isSelected && binding.eduCourseSpinner.selectedItem.toString() == "other") {
                binding.eduCourseOtherInput.text.toString()
            } else if (binding.eduCourseSpinner.isSelected) {
                binding.eduCourseSpinner.selectedItem.toString()
            } else {
                "NA"
            }
    }

    private fun changeUI(screenPointer: Int) {
        Log.d("UpdateMemberActivty", "value $screenPointer")
        when (screenPointer) {
            -1 -> {
                onBackPressed()
            }

            0 -> {
                pageUpdates()
                crossFade(
                    listOf(binding.informationPreviewPage),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage
                    )
                )
            }

            1 -> {
                registerPageUI()
                crossFade(
                    listOf(binding.registrationLayout),
                    listOf(
                        binding.occupatioBusinessPage,
                        binding.addImage,
                        binding.informationPreviewPage
                    )
                )
            }

            2 -> {
                populateSpinner()
                crossFade(
                    listOf(binding.occupatioBusinessPage),
                    listOf(
                        binding.registrationLayout,
                        binding.addImage,
                        binding.informationPreviewPage
                    )
                )
            }

            3 -> {
                crossFade(
                    listOf(binding.addImage),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.informationPreviewPage
                    )
                )
            }

            else -> {}
        }
    }

    private fun pageUpdates() {
        if (member.education == "Phd") {
            binding.previewEduDepartment.visibility = View.VISIBLE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.GONE
        } else if (member.education == "Bachelors") {
            binding.previewEduDepartment.visibility = View.VISIBLE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.VISIBLE
        } else if (member.education == "Masters") {
            binding.previewEduDepartment.visibility = View.VISIBLE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.VISIBLE
        } else {
            binding.previewEduDepartment.visibility = View.GONE
            binding.previewEduInstitute.visibility = View.GONE
            binding.previewEduAdditionalDetails.visibility = View.GONE
            binding.previewEduCourse.visibility = View.GONE
        }

        if (member.occupation == "Business") {
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisName.visibility = View.VISIBLE
            binding.previewBuisType.visibility = View.VISIBLE
            binding.previewBuisAddress.visibility = View.VISIBLE
        } else if (member.occupation == "Student" || member.occupation == "Not Working" || member.occupation == "Retired" || member.occupation == "HouseWife") {
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
        } else {
            binding.previewOccuEmployer.visibility = View.VISIBLE
            binding.previewOccuDepartment.visibility = View.VISIBLE
            binding.previewOccuPosition.visibility = View.VISIBLE
            binding.previewOccuAddress.visibility = View.VISIBLE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
        }
    }

    private fun populateSpinner() {

        if (member.education == "Junior School") {
            binding.eduLevelSpinner.setSelection(0)
        } else if (member.education == "High School") {
            binding.eduLevelSpinner.setSelection(1)
        } else if (member.education == "Higher Secondary School") {
            binding.eduLevelSpinner.setSelection(2)
        } else if (member.education == "Diploma") {
            binding.eduLevelSpinner.setSelection(3)
        } else if (member.education == "Bachelors") {
            binding.eduLevelSpinner.setSelection(4)
        } else if (member.education == "Masters") {
            binding.eduLevelSpinner.setSelection(5)
        } else if (member.education == "Phd") {
            binding.eduLevelSpinner.setSelection(6)
        }


        if (member.occupation == "Student") {
            binding.occuLevelSpinner.setSelection(0)
        } else if (member.occupation == "Government Job") {
            binding.occuLevelSpinner.setSelection(1)
        } else if (member.occupation == "Private Job") {
            binding.occuLevelSpinner.setSelection(2)
        } else if (member.occupation == "Retired") {
            binding.occuLevelSpinner.setSelection(3)
        } else if (member.occupation == "Business") {
            binding.occuLevelSpinner.setSelection(4)
        } else if (member.occupation == "Doctor") {
            binding.occuLevelSpinner.setSelection(5)
        } else if (member.occupation == "Lawyer") {
            binding.occuLevelSpinner.setSelection(6)
        } else if (member.occupation == "Chartered Accountant") {
            binding.occuLevelSpinner.setSelection(7)
        } else if (member.occupation == "Not Working") {
            binding.occuLevelSpinner.setSelection(8)
        } else if (member.occupation == "HouseWife") {
            binding.occuLevelSpinner.setSelection(9)
        }

        if (member.occupation == "Business") {
            if(member.occupationDetails.businessName.isNotEmpty())
                binding.occuBuisNameInput.setText(member.occupationDetails.businessName)
        } else {
            binding.occuBuisTypeSpinner.setSelection(4)
        }

    }

    private fun populateInformationPreview() {

        Log.e("populateInformatinPreview", member.familyID + "what ???")
        Log.e("populateInformatinPreview", member.toString() + "what ???")

        binding.previewNameinput.text = binding.nameinput.text.toString()
        binding.previewContactinput.text = binding.contactinput.text.toString()
        binding.previewDOBtext.text = binding.DOBinput.text.toString()
        binding.previewAgeSpinner.text = member.age.toString()
        binding.previewGenderSpinner.text = member.gender
        binding.previewRelationInput.text = binding.relationinput.text.toString()
        binding.previewFamilyIDinput.text = member.familyID
        binding.previewLandmarkInput.text = member.landmark
        binding.previewBloodGroupSpinner.text = member.bloodGroup
        binding.previewKaryainput.text = member.karyakarni
        binding.previewOccuLevelSpinner.text = member.occupation
        binding.previewEduLevelSpinner.text = member.education
        binding.previewEduDepartInput.text = binding.eduDepartInput.text.toString()
        binding.previewEduInstituteInput.text = binding.eduInstituteInput.text.toString()
        binding.previewEduAdditionalInput.text = binding.eduAdditionalInput.text.toString()
        binding.previewOccuEmployerInput.text = binding.occuEmployerInput.text.toString()
        binding.previewOccuDepartmentInput.text = binding.occuDepartmentInput.text.toString()
        binding.previewOccuAddressInput.text = binding.occuAddressInput.text.toString()
        binding.previewOccuPositioninput.text = binding.occuPositioninput.text.toString()
        binding.previewEduCourseInput.text = binding.coursetext.text.toString()
        Glide.with(this)
            .load(member.profilePic).into(binding.previewIvAddImageMember)

        if (member.occupation == "Business") {
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisType.visibility = View.VISIBLE
            binding.previewBuisName.visibility = View.VISIBLE
            binding.previewBuisAddress.visibility = View.VISIBLE
            binding.previewBuisTypeInput.text = member.occupationDetails.businessType
            binding.previewBuisNameInput.text = member.occupationDetails.businessName
            binding.previewBuisAddressInput.text = member.occupationDetails.businessAddress
        } else {
            binding.previewOccuDepartment.visibility = View.VISIBLE
            binding.previewOccuEmployer.visibility = View.VISIBLE
            binding.previewOccuPosition.visibility = View.VISIBLE
            binding.previewOccuAddress.visibility = View.VISIBLE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
        }
    }

    private fun populateUI() {
        Log.d("Member Populate", member.toString())
        binding.nameinput.setText(member.name)
        binding.relationinput.setText(member.relation)
        binding.IDinput.setText(member.familyID)
        binding.contactinput.setText(member.contact)
        member.occupationDetails?.let { occupationDetails ->
            binding.occuPositioninput.setText(occupationDetails.jobPost)
            binding.occuAddressInput.setText(occupationDetails.jobLocation)
            binding.occuDepartmentInput.setText(occupationDetails.jobDepartment)
            binding.occuEmployerInput.setText(occupationDetails.jobEmployer)
        }
        member.educationDetails?.let { educationDetails ->
            binding.eduDepartInput.setText(educationDetails.fieldOfStudy)
            binding.eduInstituteInput.setText(educationDetails.institute)
            binding.eduAdditionalInput.setText(educationDetails.additionalDetails)
        }
        binding.landmarkInput.setText(member.landmark)



        Glide.with(this).load(member.profilePic).into(binding.ivAddImageMember)
        binding.ivAddImageMember.setOnClickListener {
            openFilePicker()
        }

        var gender = 0
        if (member.gender != "Male") {
            gender = 1
        }

        binding.genderSpinner.setSelection(
            gender
        )

        binding.citySpinner.setSelection(0)
        binding.stateSpinner.setSelection(0)
        binding.contactinput.isEnabled = false

        populateInformationPreview()

    }

    val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                val selectedImagePath = getImagePath(uri!!).toString()
                val file = File(selectedImagePath)
                val requestBody = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
                selectedImageMultiPartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
                binding.ivAddImageMember.setImageURI(uri)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }

        }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        getImage.launch(intent)
    }

    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private fun setObservables() {
        viewModel.updatedUser.observe(this, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("Success", resources.data.toString())
                    hideProgressDialog()
                    Toast.makeText(this, "Member Updated Successfully", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }

                Resource.Status.LOADING -> {
                    showProgressDialog("Updating")
                    Log.e("Loading", resources.data.toString())
                }

                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError.toString())
                    Log.e("Error", resources.apiError.toString())
                }

                else -> {}
            }
        })

        viewModel.deleteUser.observe(this, Observer {resources ->
            when(resources.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    Toast.makeText(this, "Member Deleted Successfully", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }

                Resource.Status.LOADING -> {
                    showProgressDialog("Deleting")
                }

                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError.toString())
                }

                else -> {}
            }

        })

        viewModel.getAllKarya.observe(this, Observer {resources ->
            when(resources.status) {
                Resource.Status.SUCCESS -> {
                    setUpKaryaSpinner(resources.data!!)
                }

                Resource.Status.LOADING -> {
                }

                Resource.Status.ERROR -> {
                    showErrorSnackBar(resources.apiError.toString())
                }
            }
        })
    }

    fun showDeleteMemberDialog(context: Context, member: MemberX) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Confirm Deletion")
            setMessage("Are you sure you want to delete ${member.name}?")
            setPositiveButton("Delete") { dialog, which ->
                // Call the onDeleteConfirmed function when the user confirms deletion
                viewModel.deleteMember(familyHash,member._id)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun crossFade(visible: List<View>, invisible: List<View>) {

        for (view in visible) {
            view.apply {
                // Set the content view to 0% opacity but visible, so that it is
                // visible but fully transparent during the animation.
                alpha = 0f
                visibility = View.VISIBLE
                // Animate the content view to 100% opacity and clear any animation
                // listener set on the view.
                animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }

        for (view in invisible) {
            view.apply {
                alpha = 0f
                visibility = View.GONE

                animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }
    }

    private fun init(cityFromBackend: String, stateFromBackend: String) {
        stringArrayState = ArrayList()
        stringArrayCity = ArrayList()

        // Set city adapter
        val adapterCity = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            stringArrayCity
        )
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapterCity

        // Get state json value from assets folder
        try {
            val obj = JSONObject(loadJSONFromAssetState())
            val m_jArry = obj.getJSONArray("statelist")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val state = jo_inside.getString("State")
                stringArrayState.add(state)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            stringArrayState
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = adapter

        // Set the selected state from the backend
        val stateIndex = stringArrayState.indexOf(stateFromBackend)
        if (stateIndex != -1) {
            binding.stateSpinner.setSelection(stateIndex)
        }

        // State spinner item selected listener with the help of this we get selected value
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position)
                val text = binding.stateSpinner.selectedItem.toString()

                spinnerStateValue = binding.stateSpinner.selectedItem.toString()
                _state = spinnerStateValue
                Log.e("SpinnerStateValue", spinnerStateValue)
                stringArrayCity.clear()

                try {
                    val obj = JSONObject(loadJSONFromAssetCity())
                    val m_jArry = obj.getJSONArray("citylist")

                    for (i in 0 until m_jArry.length()) {
                        val jo_inside = m_jArry.getJSONObject(i)
                        val state = jo_inside.getString("State")

                        if (spinnerStateValue.equals(state, ignoreCase = true)) {
                            _city = jo_inside.getString("city")
                            stringArrayCity.add(_city)
                        }
                    }

                    // Notify adapter city for getting selected value according to state
                    adapterCity.notifyDataSetChanged()

                    // Set the selected city from the backend
                    val cityIndex = stringArrayCity.indexOf(cityFromBackend)
                    if (cityIndex != -1) {
                        binding.citySpinner.setSelection(cityIndex)
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerCityValue = binding.citySpinner.selectedItem.toString()
                Log.e("SpinnerCityValue", spinnerCityValue)

                _city = spinnerCityValue
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadJSONFromAssetState(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("state.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun loadJSONFromAssetCity(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("cityState.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun inito() {
        stringArrayState = ArrayList()
        stringArrayCity = ArrayList()

        // Set city adapter
        val adapterCity = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            stringArrayCity
        )
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapterCity

        // Get state json value from assets folder
        try {
            val obj = JSONObject(loadJSONFromAssetState())
            val m_jArry = obj.getJSONArray("statelist")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val state = jo_inside.getString("State")
                stringArrayState.add(state)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            stringArrayState
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = adapter

        // State spinner item selected listener with the help of this we get selected value
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent?.getItemAtPosition(position)
                val text = binding.stateSpinner.selectedItem.toString()

                spinnerStateValue = binding.stateSpinner.selectedItem.toString()
                _state = spinnerStateValue
                Log.e("SpinnerStateValue", spinnerStateValue)
                stringArrayCity.clear()

                try {
                    val obj = JSONObject(loadJSONFromAssetCity())
                    val m_jArry = obj.getJSONArray("citylist")

                    for (i in 0 until m_jArry.length()) {
                        val jo_inside = m_jArry.getJSONObject(i)
                        val state = jo_inside.getString("State")

                        if (spinnerStateValue.equals(state, ignoreCase = true)) {
                            _city = jo_inside.getString("city")
                            stringArrayCity.add(_city)
                        }
                    }

                    // Notify adapter city for getting selected value according to state
                    adapterCity.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinnerCityValue = binding.citySpinner.selectedItem.toString()
                Log.e("SpinnerCityValue", spinnerCityValue)

                _city = spinnerCityValue
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setUpKaryaSpinner(karyakarni: KaryakarniResponse) {
        val karyakarniList = karyakarni.karyakarni

        val karyakarniAdapter = KaryakarniSpinnerAdapter(
            this,
            R.layout.karyakarni_spinner_item_layout,
            karyakarniList
        )
//        karyakarniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.KaryainputSpinner.adapter = karyakarniAdapter
    }

}
package com.example.communityapp.ui.SignUp

import android.app.Activity
import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.PreferencesHelper
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.newModels.MemberDataX
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.databinding.ActivitySignUpBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedImagePath:String = ""
    private var shortAnimationDuration = 500
    private var screenPointer = 1
    var eduSpinner = 0
    var occuSpinner = 0
    var ageSpinner = 0
    var buisTypeSpinner = 0
    var courseSpinner = 0
    var headAddress = ""
    var uri: Uri = Uri.EMPTY
    var course = "NA"
    var buisType = "NA"
    private  lateinit var mImagePart: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        setWindowsUp()

        binding.addImageText.setOnClickListener {
            openFilePicker()
        }

        setObservables()
        registerPageUI()

        binding.familyIDinput.setOnClickListener {
            val name = binding.nameinput.text.toString()
            val contact = binding.contactinput.text.toString()
            if (name.isNotEmpty() && contact.isNotEmpty()){
                val familyID = "CH" + name.substring(0,3) + contact.substring(6,9)
                binding.familyIDinput.setText(familyID)
            }else{
                Toast.makeText(this, "Please enter your name and contact no", Toast.LENGTH_SHORT).show()
            }
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
                        binding.eduDepartment.visibility = View.VISIBLE
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
                            val courseAdapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_dropdown_item, list)
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter
                            binding.eduDepartment.visibility = View.VISIBLE
                            binding.eduCourseSpinner.setSelection(courseSpinner)
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
                            val courseAdapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_dropdown_item, list)
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter
                            binding.eduDepartment.visibility = View.VISIBLE
                            binding.eduCourseSpinner.setSelection(courseSpinner)
                        }else if (binding.eduLevelSpinner.selectedItem.toString() == "Phd") {
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
                    if(binding.occuLevelSpinner.selectedItem.toString() == "Not Working" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "Retired" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "HouseWife" ||
                        binding.occuLevelSpinner.selectedItem.toString() == "Student"){
                        binding.occuDepartment.visibility = View.GONE
                        binding.occuEmployer.visibility = View.GONE
                        binding.occuPosition.visibility = View.GONE
                        binding.occuAddress.visibility = View.GONE
                        binding.occuBuisName.visibility = View.GONE
                        binding.occuBuisType.visibility = View.GONE
                    }else if(binding.occuLevelSpinner.selectedItem.toString() == "Business"){
                        binding.occuEmployer.visibility = View.GONE
                        binding.occuDepartment.visibility = View.GONE
                        binding.occuPosition.visibility = View.GONE
                        binding.occuAddress.visibility = View.VISIBLE
                        binding.occuBuisName.visibility = View.VISIBLE
                        binding.occuBuisType.visibility = View.VISIBLE
                        binding.occuAddressInput.hint = "Business Address"
                        binding.occuAddressText.text = "Business Address"
                        val businessTypeList = arrayListOf("Restaurant", "Retail Store", "Tech", "Consulting Firm", "other")
                        val businessTypeAdapter = ArrayAdapter(this@SignUpActivity,
                            android.R.layout.simple_spinner_dropdown_item, businessTypeList)
                        businessTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.occuBuisTypeSpinner.adapter=businessTypeAdapter

                        binding.occuBuisTypeSpinner.setSelection(buisTypeSpinner)
                    }else{
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


    }

    private fun registerPageUI() {

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        binding.ageSpinner.setSelection(ageSpinner)

        val genderList = arrayListOf("Male", "Female")
        val genadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

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

        //add states in state spinners
        val statesList = arrayListOf("Madhya Pradesh")
        val statesadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = statesadapter

        //add cities in city spinners
        val citiesList = arrayListOf("Indore", "Bhopal", "Gwalior", "Jabalpur")
        val citiesadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, citiesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = citiesadapter

        //add blood groups in blood group spinners
        val bloodGroupList = arrayListOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "other")
        val bloodGroupadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroupList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupadapter



        val no = preferencesHelper.getContact()
        Log.d("Dashboard phone no", no.toString())


        val phoneNum = no
        binding.contactinput.setText(phoneNum)

        binding.memberSubmit.setOnClickListener {
            checkDetails1()
        }

        binding.occuBuisSubmit.setOnClickListener {
            checkDetails2()
        }

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
        }


        binding.addImageText.setOnClickListener {
            openFilePicker()
        }


        binding.familyRegistrationToPreview.setOnClickListener {
            screenPointer++
            changeUI(screenPointer)
        }

        binding.previewInfoSubmit.setOnClickListener {
            submitRegistration()
        }

        binding.previewInfoNotSubmit.setOnClickListener {
            screenPointer = 1
            changeUI(screenPointer)
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

    private fun checkDetails1() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty() && isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }
        else if(binding.ageSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
        }
        else if(binding.genderSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else if(binding.bloodGroupSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else if (binding.landmarkInput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }
        else if(binding.citySpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else if(binding.stateSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else{
            screenPointer++
            changeUI(screenPointer)
        }
    }

    private fun checkDetails2() {
        if(binding.occuLevelSpinner.selectedItem.toString().isEmpty()){
            Toast.makeText(this, "Please enter your occupation", Toast.LENGTH_SHORT).show()
        }
        else if(binding.eduLevelSpinner.selectedItem.toString().isEmpty()){
            Toast.makeText(this, "Please enter your education", Toast.LENGTH_SHORT).show()
        }
        else{
            screenPointer++
            changeUI(screenPointer)
        }

        if (binding.eduCourseSpinner.selectedItem.toString() == "other") {
            course = binding.eduCourseOtherInput.text.toString()
        } else {
            course = binding.eduCourseSpinner.selectedItem.toString()
        }
    }

    private fun changeUI(screenPointer : Int){
        when(screenPointer){

            0-> {
                onBackPressed()
            }

            1-> {
                registerPageUI()
                crossFade(
                    listOf(binding.registrationLayout),
                    listOf(
                        binding.occupatioBusinessPage,
                        binding.addImage,
                        binding.informationPreviewPage)
                )
            }

            2-> {
                populateSpinner()
                crossFade(
                    listOf(binding.occupatioBusinessPage),
                    listOf(
                        binding.registrationLayout,
                        binding.addImage,
                        binding.informationPreviewPage)
                )
            }

            3-> {
                crossFade(
                    listOf(binding.addImage),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.informationPreviewPage)
                )
            }

            4-> {
                populateInformationPreview()
                crossFade(
                    listOf(binding.informationPreviewPage),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage)
                )
            }

            else -> {}
        }
    }

    private fun populateSpinner() {
        //give logging for all spinners
        Log.d(
            "FamilyActivity",
            "Edu Spinner: $eduSpinner, Occu Spinner: $occuSpinner, Age Spinner: $ageSpinner"
        )
        binding.eduLevelSpinner.setSelection(eduSpinner)
        binding.occuLevelSpinner.setSelection(occuSpinner)
    }

    private fun populateInformationPreview() {

        binding.eduLevelSpinner.setSelection(eduSpinner)
        binding.occuLevelSpinner.setSelection(occuSpinner)

        val completeAddress =
            binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()


        binding.previewNameinput.text = binding.nameinput.text.toString()
        binding.previewContactinput.text = binding.contactinput.text.toString()
        binding.previewDOBtext.text = binding.DOBinput.text.toString()
        binding.previewAgeSpinner.text = binding.ageSpinner.selectedItem.toString()
        binding.previewGenderSpinner.text = binding.genderSpinner.selectedItem.toString()
        binding.previewRelationInput.text = "HEAD"
        binding.previewFamilyIDinput.text = binding.familyIDinput.text.toString()
        binding.previewLandmarkInput.text = completeAddress
        binding.previewBloodGroupSpinner.text = binding.bloodGroupSpinner.selectedItem.toString()
        binding.previewKaryainput.text = binding.Karyainput.text.toString()
        binding.previewOccuLevelSpinner.text = binding.occuLevelSpinner.selectedItem.toString()
        binding.previewEduLevelSpinner.text = binding.eduLevelSpinner.selectedItem.toString()
        binding.previewEduDepartInput.text = binding.eduDepartInput.text.toString()
        binding.previewEduInstituteInput.text = binding.eduInstituteInput.text.toString()
        binding.previewEduAdditionalInput.text = binding.eduAdditionalInput.text.toString()
        binding.previewOccuEmployerInput.text = binding.occuEmployerInput.text.toString()
        binding.previewOccuDepartmentInput.text = binding.occuDepartmentInput.text.toString()
        binding.previewOccuAddressInput.text = binding.occuAddressInput.text.toString()
        binding.previewOccuPositioninput.text = binding.occuPositioninput.text.toString()
        binding.previewEduCourseInput.text = course
        binding.previewIvAddImageMember.setImageURI(uri)

        if(binding.occuLevelSpinner.selectedItem.toString() == "Business"){
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisType.visibility = View.VISIBLE
            binding.previewBuisName.visibility = View.VISIBLE
            binding.previewBuisAddress.visibility = View.VISIBLE
            binding.previewBuisTypeInput.text = binding.occuBuisTypeSpinner.selectedItem.toString()
            binding.previewBuisNameInput.text = binding.occuBuisNameInput.text.toString()
            binding.previewBuisAddressInput.text = binding.occuAddressInput.text.toString()
        }else{

            binding.previewOccuDepartment.visibility = View.VISIBLE
            binding.previewOccuEmployer.visibility = View.VISIBLE
            binding.previewOccuPosition.visibility = View.VISIBLE
            binding.previewOccuAddress.visibility = View.VISIBLE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
        }
        pageUpdates()

    }

    private fun pageUpdates() {
        if (binding.eduLevelSpinner.selectedItem.toString() == "Phd"){
            binding.previewEduDepartment.visibility = View.VISIBLE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.GONE
        }
        else if (binding.eduLevelSpinner.selectedItem.toString() == "Bachelors") {
            binding.previewEduDepartment.visibility = View.VISIBLE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.VISIBLE
        } else if (binding.eduLevelSpinner.selectedItem.toString() == "Masters") {
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

        if (binding.occuLevelSpinner.selectedItem.toString() == "Business") {
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisType.visibility = View.VISIBLE
            binding.previewBuisName.visibility = View.VISIBLE
            binding.previewBuisAddress.visibility = View.VISIBLE
        } else if (binding.occuLevelSpinner.selectedItem.toString() == "Not Working" ||
            binding.occuLevelSpinner.selectedItem.toString() == "Retired" ||
            binding.occuLevelSpinner.selectedItem.toString() == "HouseWife" ||
            binding.occuLevelSpinner.selectedItem.toString() == "Student") {
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
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

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = """^\+91\d{10}$""".toRegex()

        val matchResult = pattern.find(phoneNumber)

        return matchResult != null
    }

    private fun submitRegistration() {
        val completeAddress= binding.landmarkInput.text.toString() + ", " + binding.citySpinner.selectedItem.toString() + ", " + binding.stateSpinner.selectedItem.toString()
        var contact="NA"
        if(binding.contactinput.text.toString() != "+91"){
            contact = binding.contactinput.text.toString()
        }

        var karyakanri = "NA"
        if(binding.Karyainput.text.isNotEmpty()){
            karyakanri = binding.Karyainput.text.toString()
        }

        val memberData = MemberDataX(
            name = binding.nameinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            age = binding.ageSpinner.selectedItem.toString(),
            gender = binding.genderSpinner.selectedItem.toString(),
            karyakarni = karyakanri,
            relation = "head",
            occupation = binding.occuLevelSpinner.selectedItem.toString(),
            bloodGroup = binding.bloodGroupSpinner.selectedItem.toString(),
            profilePic = "NA",
            education = binding.eduLevelSpinner.selectedItem.toString(),
            course = if (binding.eduDepartInput.text.isNotEmpty()) binding.eduDepartInput.text.toString() else "NA",
            institute = if (binding.eduInstituteInput.text.isNotEmpty()) binding.eduInstituteInput.text.toString() else "NA",
            additionalDetails = if (binding.eduAdditionalInput.text.isNotEmpty()) binding.eduAdditionalInput.text.toString() else "NA",
            jobEmployer = if (binding.occuEmployerInput.text.isNotEmpty()) binding.occuEmployerInput.text.toString() else "NA",
            jobDepartment = if (binding.occuDepartmentInput.text.isNotEmpty()) binding.occuDepartmentInput.text.toString() else "NA",
            jobLocation = if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA",
            jobPost = if (binding.occuPositioninput.text.isNotEmpty()) binding.occuPositioninput.text.toString() else "NA",
            businessType = buisType,
            businessName = if (binding.occuBuisNameInput.text.isNotEmpty()) binding.occuBuisNameInput.text.toString() else "NA",
            businessAddress = if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA",
            fieldOfStudy = course,
            city = binding.citySpinner.selectedItem.toString(),
            state = binding.stateSpinner.selectedItem.toString(),
            landmark = binding.landmarkInput.text.toString(),
            contactVisibility = true
        )


        val data = Member(
            familyID = binding.familyIDinput.text.toString(),
            name = binding.nameinput.text.toString(),
            DOB = binding.DOBinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            age = binding.ageSpinner.selectedItem.toString().toInt(),
            gender = binding.genderSpinner.selectedItem.toString(),
            address = completeAddress,
            karyakarni = karyakanri,
            relation = "head",
            occupation = binding.occuLevelSpinner.selectedItem.toString(),
            bloodGroup = binding.bloodGroupSpinner.selectedItem.toString(),
            profilePic = "NA",
            highestEducation = binding.eduLevelSpinner.selectedItem.toString(),
            branch = if (binding.eduDepartInput.text.isNotEmpty()) binding.eduDepartInput.text.toString() else "NA",
            institute = if (binding.eduInstituteInput.text.isNotEmpty()) binding.eduInstituteInput.text.toString() else "NA",
            additionalDetails = if (binding.eduAdditionalInput.text.isNotEmpty()) binding.eduAdditionalInput.text.toString() else "NA",
            employer = if (binding.occuEmployerInput.text.isNotEmpty()) binding.occuEmployerInput.text.toString() else "NA",
            department = if (binding.occuDepartmentInput.text.isNotEmpty()) binding.occuDepartmentInput.text.toString() else "NA",
            location = if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA",
            post = if (binding.occuPositioninput.text.isNotEmpty()) binding.occuPositioninput.text.toString() else "NA",
            buisType = buisType,
            buisName = if (binding.occuBuisNameInput.text.isNotEmpty()) binding.occuBuisNameInput.text.toString() else "NA",
            course = course
        )

        val signupRequest= SignupRequest(binding.familyIDinput.text.toString(),memberData)

        viewModel.addMember(signupRequest,mImagePart,this)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate.set(year, month, dayOfMonth)
                updateSelectedDateText()
                updateAgeFromDOB()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Optional: Set a maximum date

        datePickerDialog.show()
    }

    fun isDateInCorrectFormat(dateString: String, dateFormat: String): Boolean {
        try {
            val sdf = SimpleDateFormat(dateFormat)
            sdf.isLenient = false
            val date = sdf.parse(dateString)
            return date != null && sdf.format(date) == dateString
        } catch (e: Exception) {
            return false
        }
    }

    private fun updateSelectedDateText() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        binding.DOBinput.setText(formattedDate)
    }

    private fun updateAgeFromDOB() {
        val currentDate = Calendar.getInstance()
        var age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)

        // Optionally, you can use the selected month and day to refine the age calculation
        if (currentDate.get(Calendar.MONTH) < selectedDate.get(Calendar.MONTH) ||
            (currentDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                    currentDate.get(Calendar.DAY_OF_MONTH) < selectedDate.get(Calendar.DAY_OF_MONTH))) {
            // Subtract 1 year if the birth date hasn't occurred yet this year
            age--
        }

        // Set the calculated age to the Spinner
        binding.ageSpinner.setSelection(age - 1) // Subtract 1 since age starts from 1
    }


    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
             uri = data?.data!!

            // Validate file type here if needed before proceeding to upload
            if (isFileTypeAllowed(uri)) {
                // Proceed with uploading the file
                val selectedImagePath = getImagePath(uri).toString()
                binding.ivAddImageMember.setImageURI(uri)

                val file = File(selectedImagePath)
                Log.e("ImageFile", file.path)

                val requestBody = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

                mImagePart = body
            } else {
                // Handle case where file type is not allowed
                Toast.makeText(this, "Only JPEG, JPG, PNG, GIF files are allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isFileTypeAllowed(uri: Uri): Boolean {
        val contentResolver = applicationContext.contentResolver
        val mimeType = contentResolver.getType(uri)
        return mimeType != null && mimeType.startsWith("image/")
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
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

    private fun setObservables(){
        viewModel.user.observe(this, Observer {resources ->

            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    Log.e("Success",resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.text.clear()
                    binding.landmarkInput.text.clear()
                    binding.DOBinput.text.clear()
                    binding.ageSpinner.setSelection(1)
                    binding.genderSpinner.setSelection(1)


                    viewModel.signInWithPhone(preferencesHelper.getContact().toString())



//                    Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this,DashboardActivity::class.java))
//                    finish()
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Adding Your Details as Family Head ...")
                    Log.e("Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    Log.e("Error",resources.apiError.toString())
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                }
                else -> {}
            }
        })

        viewModel.loginStatusPhone.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    // start DashboardActivity after otp verification
                    Log.e("JWTToken", resource.data?.token.toString())
                    //save to shared pref
                    preferencesHelper.setToken( resource.data?.token.toString())

                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra(Constants.USERNAME,preferencesHelper.getContact() )
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        binding.logoImage,
                        getString(R.string.transition_name)
                    ).toBundle()
                    startActivity(intent, options)
                    finish()
                }

                Resource.Status.ERROR -> {
                    Log.e("JWTToken Error", "what is it " + resource.apiError)
                    showErrorSnackBar("Error: ${resource.apiError?.message}")
                }

                Resource.Status.LOADING -> {
                    Log.e("JWTToken", "loading")

                }

                else -> {
                    Log.e("JWTToken", "else")
                }
            }

        })

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
}
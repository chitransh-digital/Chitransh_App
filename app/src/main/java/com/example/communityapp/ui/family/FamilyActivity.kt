package com.example.communityapp.ui.family

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.e
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
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityFamilyBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FamilyActivity : BaseActivity() {

    private lateinit var viewModel: FamilyViewModel
    private lateinit var binding: ActivityFamilyBinding
    private lateinit var family_id: String
    private var selectedDate: Calendar = Calendar.getInstance()
    private var familyMember = "other"
    private var selectedImagePath: String = ""
    private var uniqueRelations: List<String>? = null
    private var screenPointer = 0
    private var shortAnimationDuration = 500
    var uri: Uri? = null
    var eduSpinner = 0
    var occuSpinner = 0
    var ageSpinner = 0
    var buisTypeSpinner = 0
    var courseSpinner = 0
    var headAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()
        setWindowsUp()
        getArguements()
        changeUI(screenPointer)
        e("uniqueRelations", uniqueRelations.toString())

        if (uniqueRelations?.contains("Wife") == true) {
            binding.relationshipSelection1.btnWife.visibility = View.GONE
        }
        if (uniqueRelations?.contains("Husband") == true) {
            binding.relationshipSelection1.btnHusband.visibility = View.GONE
        }
        if (uniqueRelations?.contains("Father") == true) {
            binding.relationshipSelection1.btnFather.visibility = View.GONE
        }
        if (uniqueRelations?.contains("Mother") == true) {
            binding.relationshipSelection1.btnMother.visibility = View.GONE
        }

        binding.relationshipSelection1.btnWife.setOnClickListener {
            familyMember = "Wife"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnHusband.setOnClickListener {
            familyMember = "Husband"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnSon.setOnClickListener {
            familyMember = "Son"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnDaughter.setOnClickListener {
            familyMember = "Daughter"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnFather.setOnClickListener {
            familyMember = "Father"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnMother.setOnClickListener {
            familyMember = "Mother"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnOther.setOnClickListener {
            screenPointer++
            changeUI(screenPointer)
        }

        binding.familyBack.setOnClickListener {
            screenPointer--
            changeUI(screenPointer)
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
                            val courseAdapter = ArrayAdapter(this@FamilyActivity, android.R.layout.simple_spinner_dropdown_item, list)
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter
                            binding.eduDepartment.visibility = View.GONE
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
                            val courseAdapter = ArrayAdapter(this@FamilyActivity, android.R.layout.simple_spinner_dropdown_item, list)
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.eduCourseSpinner.adapter = courseAdapter
                            binding.eduDepartment.visibility = View.GONE
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
                        val businessTypeAdapter = ArrayAdapter(this@FamilyActivity,
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

        binding.eduCourseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

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

    private fun changeUI(screenPointer: Int) {
        when (screenPointer) {
            -1 -> onBackPressed()

            0 -> {
                crossFade(
                    listOf(binding.relationshipSelection1.relationshipSelectionLayout),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage,
                        binding.informationPreviewPage
                    )
                )
            }

            1 -> {
                registerPageUI()
                crossFade(
                    listOf(binding.registrationLayout),
                    listOf(
                        binding.relationshipSelection1.relationshipSelectionLayout,
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
                        binding.relationshipSelection1.relationshipSelectionLayout,
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
                        binding.relationshipSelection1.relationshipSelectionLayout,
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.informationPreviewPage
                    )
                )
            }

            4 -> {
                populateInformationPreview()
                crossFade(
                    listOf(binding.informationPreviewPage),
                    listOf(
                        binding.relationshipSelection1.relationshipSelectionLayout,
                        binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage
                    )
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

        val completeAddress = if(binding.sameAsHead.isChecked){
            headAddress
        }else{
            binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()
        }

        val course = "NA"
        if (binding.eduCourseSpinner.isSelected && binding.eduCourseSpinner.selectedItem.toString() == "other") {
            binding.eduCourseOtherInput.text.toString()
        } else if(binding.eduCourseSpinner.isSelected){
            binding.eduCourseSpinner.selectedItem.toString()
        }

        binding.previewNameinput.text = binding.nameinput.text.toString()
        binding.previewContactinput.text = binding.contactinput.text.toString()
        binding.previewDOBtext.text = binding.DOBinput.text.toString()
        binding.previewAgeSpinner.text = binding.ageSpinner.selectedItem.toString()
        binding.previewGenderSpinner.text = binding.genderSpinner.selectedItem.toString()
        binding.previewRelationInput.text = binding.relationinput.text.toString()
        binding.previewFamilyIDinput.text = binding.IDinput.text.toString()
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
            binding.previewEduDepartment.visibility = View.GONE
            binding.previewEduInstitute.visibility = View.VISIBLE
            binding.previewEduAdditionalDetails.visibility = View.VISIBLE
            binding.previewEduCourse.visibility = View.VISIBLE
        } else if (binding.eduLevelSpinner.selectedItem.toString() == "Masters") {
            binding.previewEduDepartment.visibility = View.GONE
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

    private fun registerPageUI() {

        binding.headAddress.text = "Address of Head : $headAddress"
        Log.d("FamilyActivity", "Head Address: $headAddress")

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

        if (familyMember == "Father") {
            binding.genderSpinner.setSelection(0)
            binding.genderSpinner.isEnabled = false
        } else if (familyMember == "Mother") {
            binding.genderSpinner.setSelection(1)
            binding.genderSpinner.isEnabled = false
        } else if (familyMember == "Son") {
            binding.genderSpinner.setSelection(0)
            binding.genderSpinner.isEnabled = false
        } else if (familyMember == "Daughter") {
            binding.genderSpinner.setSelection(1)
            binding.genderSpinner.isEnabled = false
        } else if (familyMember == "Husband") {
            binding.genderSpinner.setSelection(0)
            binding.genderSpinner.isEnabled = false
        } else if (familyMember == "Wife") {
            binding.genderSpinner.setSelection(1)
            binding.genderSpinner.isEnabled = false
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

        val relationList = arrayListOf("Brother", "Sister", "GrandMother", "GrandFather")
        val relationadapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, relationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.relationSpinner.adapter = relationadapter

        val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
        val no = sharedPreferences.getString(Constants.PHONE_NUMBER, null)

        binding.relationinput.setText(familyMember)

        Log.d("Dashboard phone no", no.toString())

        binding.memberSubmit.setOnClickListener {
            checkDetails1()
        }

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
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
            Toast.makeText(this, "Please enter your relation", Toast.LENGTH_SHORT).show()
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
            if (binding.eduDepartInput.text.isNotEmpty()) binding.eduDepartInput.setText(
                capitalizeNames(binding.eduDepartInput.text.toString())
            )
            if (binding.eduInstituteInput.text.isNotEmpty()) binding.eduInstituteInput.setText(
                capitalizeNames(binding.eduInstituteInput.text.toString())
            )
            if (binding.eduAdditionalInput.text.isNotEmpty()) binding.eduAdditionalInput.setText(
                capitalizeNames(binding.eduAdditionalInput.text.toString())
            )
            if (binding.occuEmployerInput.text.isNotEmpty()) binding.occuEmployerInput.setText(
                capitalizeNames(binding.occuEmployerInput.text.toString())
            )
            if (binding.occuDepartmentInput.text.isNotEmpty()) binding.occuDepartmentInput.setText(
                capitalizeNames(binding.occuDepartmentInput.text.toString())
            )
            if (binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.setText(
                capitalizeNames(binding.occuAddressInput.text.toString())
            )
            if (binding.occuPositioninput.text.isNotEmpty()) binding.occuPositioninput.setText(
                capitalizeNames(binding.occuPositioninput.text.toString())
            )
            screenPointer++
            changeUI(screenPointer)
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = """^\+91\d{10}$""".toRegex()

        val matchResult = pattern.find(phoneNumber)

        return matchResult != null
    }

    private fun submitRegistration() {
        val completeAddress = if(binding.sameAsHead.isChecked){
            headAddress
        }else{
            binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()
        }
        var contact = "NA"
        if (binding.contactinput.text.toString() != "+91") {
            contact = binding.contactinput.text.toString()
        }
        var education = binding.eduLevelSpinner.selectedItem.toString()
        if (binding.eduInstituteInput.text.isNotEmpty()) {
            education += "," + binding.eduInstituteInput.text.toString()
        }

        var karyakanri = "NA"
        if (binding.Karyainput.text.isNotEmpty()) {
            karyakanri = binding.Karyainput.text.toString()
        }

        val course = "NA"
        if (binding.eduCourseSpinner.isSelected && binding.eduCourseSpinner.selectedItem.toString() == "other") {
            binding.eduCourseOtherInput.text.toString()
        } else if(binding.eduCourseSpinner.isSelected) {
            binding.eduCourseSpinner.selectedItem.toString()
        }

        val buisType = "NA"
        if (binding.occuBuisTypeSpinner.isSelected) {
            binding.occuBuisTypeSpinner.selectedItem.toString()
        }

        val data = Member(
            familyID = binding.IDinput.text.toString(),
            name = binding.nameinput.text.toString(),
            DOB = binding.DOBinput.text.toString(),
            contact = contact,
            age = binding.ageSpinner.selectedItem.toString().toInt(),
            gender = binding.genderSpinner.selectedItem.toString(),
            address = completeAddress,
            karyakarni = karyakanri,
            relation = binding.relationinput.text.toString(),
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
        showProgressDialog("Adding Member...")
        viewModel.addMember(member = data, selectedImagePath)
    }

    private fun getArguements() {
        family_id = intent.getStringExtra(Constants.FAMILYID).toString()
        binding.IDinput.setText(family_id)
        uniqueRelations = intent.getStringArrayListExtra(Constants.UNIQUE_RELATIONS)
        headAddress = intent.getStringExtra(Constants.HEAD_ADDRESS).toString()
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

    private fun setObservables() {
        viewModel.user.observe(this, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    Log.e("Success", resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.setText("+91")
                    binding.landmarkInput.text.clear()
                    binding.Karyainput.text.clear()
                    binding.DOBinput.text.clear()
                    binding.ageSpinner.setSelection(1)
                    binding.genderSpinner.setSelection(1)
                    binding.ivAddImageMember.setImageResource(R.drawable.account_circle)
                    selectedImagePath = ""
                    Toast.makeText(this, "Member Added Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }

                Resource.Status.LOADING -> {
                    Log.e("Loading", resources.data.toString())
                }

                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    Log.e("Error", resources.apiError.toString())
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                }

                else -> {}
            }
        })
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate.set(year, month, dayOfMonth)
                updateSelectedDateText()
//                updateAgeFromDOB()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate =
            System.currentTimeMillis() // Optional: Set a maximum date

        datePickerDialog.show()
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

    val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                uri = data?.data
                selectedImagePath = getImagePath(uri!!).toString()
                Log.d("Image Path", "The image is $selectedImagePath and the uri is $uri")
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
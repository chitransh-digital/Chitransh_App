package com.example.communityapp.ui.family

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class FamilyActivity : BaseActivity() {

    private lateinit var viewModel: FamilyViewModel
    private lateinit var binding: ActivityFamilyBinding
    private lateinit var family_id : String
    private var selectedDate: Calendar = Calendar.getInstance()
    private var familyMember="other"
    private var selectedImagePath:String = ""
    private var uniqueRelations: List<String>? = null
    private var screenPointer = 0
    private var shortAnimationDuration = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()
        setWindowsUp()
        getArguements()
        changeUI(screenPointer)
        e("uniqueRelations",uniqueRelations.toString())

        if(uniqueRelations?.contains("wife") == true){
            binding.relationshipSelection1.btnWife.visibility = View.GONE
        }
        if(uniqueRelations?.contains("husband") == true){
            binding.relationshipSelection1.btnHusband.visibility = View.GONE
        }
        if(uniqueRelations?.contains("father") == true){
            binding.relationshipSelection1.btnFather.visibility = View.GONE
        }
        if(uniqueRelations?.contains("mother") == true){
            binding.relationshipSelection1.btnMother.visibility = View.GONE
        }

        binding.relationshipSelection1.btnWife.setOnClickListener {
            familyMember = "wife"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnHusband.setOnClickListener {
            familyMember = "husband"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnSon.setOnClickListener {
            familyMember = "son"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnDaughter.setOnClickListener {
            familyMember = "daughter"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnFather.setOnClickListener {
            familyMember = "father"
            screenPointer++
            changeUI(screenPointer)
        }

        binding.relationshipSelection1.btnMother.setOnClickListener {
            familyMember = "mother"
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

        binding.eduLevelSpinner.setOnClickListener {
            if(binding.eduLevelSpinner.selectedItem.toString() == "High School" ||
                binding.eduLevelSpinner.selectedItem.toString() == "Higher Secondary School"){
                binding.eduDepartment.visibility = View.VISIBLE
                binding.eduInstitute.visibility = View.VISIBLE
                binding.eduAdditionalDetails.visibility = View.VISIBLE
            }else {
                binding.eduDepartment.visibility = View.GONE
                binding.eduInstitute.visibility = View.GONE
                binding.eduAdditionalDetails.visibility = View.GONE
            }

        }

        binding.occuLevelSpinner.setOnClickListener {
            if(binding.eduLevelSpinner.selectedItem.toString() == "Government Job" ||
                binding.eduLevelSpinner.selectedItem.toString() == "Private Job"){
                binding.occuDepartment.visibility = View.VISIBLE
                binding.occuEmployer.visibility = View.VISIBLE
            }else {
                binding.occuDepartment.visibility = View.GONE
                binding.occuEmployer.visibility = View.GONE
            }

        }

    }

    private fun changeUI(screenPointer : Int){
        when(screenPointer){
            -1-> onBackPressed()

            0-> {
                crossFade(
                    listOf(binding.relationshipSelection1.relationshipSelectionLayout),
                    listOf(binding.registrationLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage)
                    )
            }

            1-> {
                registerPageUI()
                crossFade(
                    listOf(binding.registrationLayout),
                    listOf(binding.relationshipSelection1.relationshipSelectionLayout,
                        binding.occupatioBusinessPage,
                        binding.addImage)
                )
            }

            2-> {
                crossFade(
                    listOf(binding.occupatioBusinessPage),
                    listOf(binding.relationshipSelection1.relationshipSelectionLayout,
                        binding.registrationLayout,
                        binding.addImage)
                )
            }

            3-> {
                crossFade(
                    listOf(binding.addImage),
                    listOf(binding.relationshipSelection1.relationshipSelectionLayout,
                        binding.registrationLayout,
                        binding.occupatioBusinessPage)
                )
            }

            else -> {}
        }
    }

    private fun registerPageUI(){

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male","Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item                                                                                                                                                                                  , genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

        if(familyMember =="father"){
            binding.genderSpinner.setSelection(0)
        }
        else if(familyMember =="mother"){
            binding.genderSpinner.setSelection(1)
        }
        else if(familyMember =="son"){
            binding.genderSpinner.setSelection(0)
        }
        else if(familyMember =="daughter"){
            binding.genderSpinner.setSelection(1)
        }
        else if(familyMember =="husband"){
            binding.genderSpinner.setSelection(0)
        }
        else if(familyMember =="wife"){
            binding.genderSpinner.setSelection(1)
        }
        else{
            binding.genderSpinner.setSelection(0)
        }

        if(familyMember =="other"){
            binding.relationSpinner.visibility = View.VISIBLE
            binding.relationinput.visibility = View.GONE
        }else{
            binding.relationSpinner.visibility = View.GONE
            binding.relationinput.visibility = View.VISIBLE
        }

        val occupationList = arrayListOf("Student","Government Job","Private Job","Retired","Business","Doctor","Lawyer","Chartered Accountant","Not Working")
        if(familyMember =="mother" || familyMember =="wife" || familyMember =="daughter"){
            occupationList.add("HouseWife")
        }

        val occupationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, occupationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.occuLevelSpinner.adapter = occupationadapter

        val educationList = arrayListOf("High School","Higher Secondary School","Bachelors","Masters","Phd")
        val educationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, educationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eduLevelSpinner.adapter = educationadapter

        //add states in state spinners
        val statesList = arrayListOf("Madhya Pradesh")
        val statesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = statesadapter

        //add cities in city spinners
        val citiesList = arrayListOf("Indore","Bhopal","Gwalior","Jabalpur" )
        val citiesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, citiesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = citiesadapter

        //add blood groups in blood group spinners
        val bloodGroupList = arrayListOf("A+","A-","B+","B-","AB+","AB-","O+","O-","other")
        val bloodGroupadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroupList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupadapter

        val relationList = arrayListOf("Brother","Sister","GrandParents")
        val relationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, relationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.relationSpinner.adapter = relationadapter

        val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
        val no = sharedPreferences.getString(Constants.PHONE_NUMBER, null)

        binding.relationinput.setText(familyMember)

        Log.d("Dashboard phone no",no.toString())

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

        binding.familyRegistrationSubmit.setOnClickListener {
            submitRegistration()
        }
    }

    private fun checkDetails1() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(!isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter valid contact no", Toast.LENGTH_SHORT).show()
        }
        else if(binding.ageSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
        }
        else if(binding.genderSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else if(binding.relationinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your relation", Toast.LENGTH_SHORT).show()
        }
        else if(binding.IDinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your FamilyID", Toast.LENGTH_SHORT).show()
        }
        else if(binding.bloodGroupSpinner.selectedItem.toString().isEmpty()){
            Toast.makeText(this, "Please enter your relation", Toast.LENGTH_SHORT).show()
        }
        else{
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
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = """^\+91\d{10}$""".toRegex()
        if(phoneNumber == "+91"){
            return true
        }

        val matchResult = pattern.find(phoneNumber)

        return matchResult != null
    }

    private fun submitRegistration() {
        val completeAddress= binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()
        var contact="NA"
        if(binding.contactinput.text.toString() != "+91"){
            contact = binding.contactinput.text.toString()
        }
        var education = binding.eduLevelSpinner.selectedItem.toString()
        if(binding.eduInstituteInput.text.isNotEmpty()){
            education += ","+binding.eduInstituteInput.text.toString()
        }

        var karyakanri = "NA"
        if(binding.Karyainput.text.isNotEmpty()){
            karyakanri = binding.Karyainput.text.toString()
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
            branch = if(binding.eduDepartInput.text.isNotEmpty()) binding.eduDepartInput.text.toString() else "NA",
            institute = if(binding.eduInstituteInput.text.isNotEmpty()) binding.eduInstituteInput.text.toString() else "NA",
            additionalDetails = if(binding.eduAdditionalInput.text.isNotEmpty()) binding.eduAdditionalInput.text.toString() else "NA",
            employer = if(binding.occuEmployerInput.text.isNotEmpty()) binding.occuEmployerInput.text.toString() else "NA",
            department = if(binding.occuDepartmentInput.text.isNotEmpty()) binding.occuDepartmentInput.text.toString() else "NA",
            location = if(binding.occuAddressInput.text.isNotEmpty()) binding.occuAddressInput.text.toString() else "NA",
            post = if(binding.occuPositioninput.text.isNotEmpty()) binding.occuPositioninput.text.toString() else "NA",
        )
        showProgressDialog("Adding Member...")
        viewModel.addMember(member = data,selectedImagePath)
    }

    private fun getArguements(){
        family_id = intent.getStringExtra(Constants.FAMILYID).toString()
        binding.IDinput.setText(family_id)
        uniqueRelations = intent.getStringArrayListExtra(Constants.UNIQUE_RELATIONS)
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

    private fun setObservables(){
        viewModel.user.observe(this, Observer {resources ->
            hideProgressDialog()
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.setText("+91")
                    binding.landmarkInput.text.clear()
                    binding.Karyainput.text.clear()
                    binding.DOBinput.text.clear()
                    binding.ageSpinner.setSelection(1)
                    binding.genderSpinner.setSelection(1)
                    binding.ivAddImageMember.setImageResource(R.drawable.account_circle)
                    selectedImagePath=""
                    Toast.makeText(this, "Member Added Successfully", Toast.LENGTH_SHORT).show()
                }
                Resource.Status.LOADING -> {
                    Log.e("Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("Error",resources.apiError.toString())
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
                updateAgeFromDOB()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Optional: Set a maximum date

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

    val getImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            selectedImagePath = getImagePath(uri!!).toString()
            Log.d("Image Path","The image is $selectedImagePath and the uri is $uri")
            binding.ivAddImageMember.setImageURI(uri)
        }else{
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
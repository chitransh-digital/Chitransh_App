package com.example.communityapp.ui.SignUp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.ActivitySignUpBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedImagePath:String = ""
    private var shortAnimationDuration = 500
    private var screenPointer = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        setWindowsUp()

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male","Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

//        binding.dateSelector.setOnClickListener {
//            showDatePickerDialog()
//        }
        binding.addImageText.setOnClickListener {
            openFilePicker()
        }

        setObservables()

        val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
        val phoneNum = sharedPreferences.getString(Constants.PHONE_NUMBER, null)
        binding.contactinput.setText(phoneNum)

        binding.memberSubmit.setOnClickListener {
            checkDetails1()
        }

        binding.occuBuisSubmit.setOnClickListener {
            checkDetails2()
        }

        binding.familyRegistrationSubmit.setOnClickListener {
            submitRegistration()
        }

        binding.familyIDinput.setOnClickListener {
            val name = binding.nameinput.text.toString()
            val contact = binding.contactinput.text.toString()
            if (name.isNotEmpty() && contact.isNotEmpty()){
                val familyID = "CH" + name.substring(0,3) + contact.substring(9,12)
                binding.familyIDinput.setText(familyID)
            }else{
                Toast.makeText(this, "Please enter your name and contact no", Toast.LENGTH_SHORT).show()
            }
        }

        //add states in state spinners
        val statesList = arrayListOf("Madhya Pradesh")
        val statesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = statesadapter

        //add cities in city spinners
        val citiesList = arrayListOf("Indore","Bhopal","Gwalior","Jabalpur" )
        val citiesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = citiesadapter

        val occupationList = arrayListOf("Government Job","Student","Retired","Business","HouseWife","Other")
        val occupationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, occupationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.occuLevelSpinner.adapter = occupationadapter


        //add blood groups in blood group spinners
        val bloodGroupList = arrayListOf("A+","A-","B+","B-","AB+","AB-","O+","O-","other")
        val bloodGroupadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupadapter

        val educationList = arrayListOf("10th","12th","Bachelor's","Master's","Phd")
        val educationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, educationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.eduLevelSpinner.adapter = educationadapter
    }

    private fun checkDetails1() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty() && isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }
//        else if(binding.DOBinput.text.isNullOrEmpty()) {
//            Toast.makeText(this, "Please enter your Date of Birth no", Toast.LENGTH_SHORT).show()
//        }
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
    }

    private fun changeUI(screenPointer : Int){
        when(screenPointer){

            0-> {
                onBackPressed()
            }

            1-> {
                crossFade(
                    listOf(binding.registrationLayout),
                    listOf(
                        binding.occupatioBusinessPage,
                        binding.addImage)
                )
            }

            2-> {
                crossFade(
                    listOf(binding.occupatioBusinessPage),
                    listOf(
                        binding.registrationLayout,
                        binding.addImage)
                )
            }

            3-> {
                crossFade(
                    listOf(binding.addImage),
                    listOf(
                        binding.registrationLayout,
                        binding.occupatioBusinessPage)
                )
            }

            else -> {}
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
        val data = Member(
            familyID = binding.familyIDinput.text.toString(),
            name = binding.nameinput.text.toString(),
            DOB = binding.DOBinput.text.toString(),
            contact = contact,
            age = binding.ageSpinner.selectedItem.toString().toInt(),
            gender = binding.genderSpinner.selectedItem.toString(),
            address = completeAddress,
            karyakarni = karyakanri,
            relation = "HEAD",
            occupation = binding.occuLevelSpinner.selectedItem.toString(),
            bloodGroup = binding.bloodGroupSpinner.selectedItem.toString(),
            profilePic = "NA",
            education = "NA"
        )
        showProgressDialog("Please wait...")
        viewModel.addMember(member = data,selectedImagePath)
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


    private val getImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("Image Path", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            selectedImagePath = getImagePath(uri!!).toString()
            binding.ivAddImageMember.setImageURI(uri)
            Log.d("Image Path", selectedImagePath)
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }

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
            hideProgressDialog()
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.text.clear()
                    binding.landmarkInput.text.clear()
                    binding.DOBinput.text.clear()
                    binding.ageSpinner.setSelection(1)
                    binding.genderSpinner.setSelection(1)
                    Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,DashboardActivity::class.java))
                    finish()
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
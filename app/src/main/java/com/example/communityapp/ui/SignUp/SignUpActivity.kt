package com.example.communityapp.ui.SignUp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.ActivitySignUpBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedImagePath:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male","Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
        }
        binding.ivAddImage.setOnClickListener {
            openFilePicker()
        }

        setObservables()

        val phoneNum = intent.getStringExtra(Constants.PHONE_NUM)
        binding.contactinput.setText(phoneNum)

        binding.memberSubmit.setOnClickListener {
            checkDetails()
        }
    }

    private fun checkDetails() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty() && isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.Addinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your address no", Toast.LENGTH_SHORT).show()
        }else if(binding.DOBinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your Date of Birth no", Toast.LENGTH_SHORT).show()
        }
        else if(binding.ageSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show()
        }
        else if(binding.genderSpinner.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show()
        }
        else if(selectedImagePath.isEmpty()) {
            Toast.makeText(this, "Please select your image", Toast.LENGTH_SHORT).show()
        }
        else{
            submitRegistration()
        }
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = """^\+91\d{10}$""".toRegex()

        val matchResult = pattern.find(phoneNumber)

        return matchResult != null
    }

    private fun submitRegistration() {
        val data = Member(
            familyID = binding.familyIDinput.text.toString(),
            name = binding.nameinput.text.toString(),
            DOB = binding.DOBinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            age = binding.ageSpinner.selectedItem.toString().toInt(),
            gender = binding.genderSpinner.selectedItem.toString(),
            address = binding.Addinput.text.toString(),
            karyakarni = "null",
            relation = "Head",
            bloodGroup = "NA",
            occupation = "NA",
            education = "NA"
        )
        viewModel.addMember(member = data, selectedImagePath)
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


    val getImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            selectedImagePath = getImagePath(uri!!).toString()
            binding.ivAddImage.setImageURI(uri)
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
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.text.clear()
                    binding.Addinput.text.clear()
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
                }
                else -> {}
            }
        })
    }
}
package com.example.communityapp.ui.family

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityBusinessBinding
import com.example.communityapp.databinding.ActivityFamilyBinding
import com.example.communityapp.ui.Dashboard.ProfileFragment
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.nio.channels.MembershipKey
import java.util.Locale

@AndroidEntryPoint
class FamilyActivity : AppCompatActivity() {

    private lateinit var viewModel: FamilyViewModel
    private lateinit var binding: ActivityFamilyBinding
    private lateinit var family_id : String
    private var selectedDate: Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()

        getArguements()

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val no = FirebaseAuth.getInstance().currentUser?.phoneNumber

        Log.d("Dashboard phoe no",no.toString())

        binding.memberSubmit.setOnClickListener {
            checkDetails()
        }

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
        }

        binding.familyBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun checkDetails() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty() && isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.Addinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your address no", Toast.LENGTH_SHORT).show()
        }else if(binding.Karyainput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your Karyakarni no", Toast.LENGTH_SHORT).show()
        }else if(binding.DOBinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your Date of Birth no", Toast.LENGTH_SHORT).show()
        }
        else if(binding.ageinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your age no", Toast.LENGTH_SHORT).show()
        }
        else if(binding.genderinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your gender no", Toast.LENGTH_SHORT).show()
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
            familyID = binding.IDinput.text.toString(),
            name = binding.nameinput.text.toString(),
            DOB = binding.DOBinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            age = binding.ageinput.text.toString().toInt(),
            gender = binding.genderinput.text.toString(),
            address = binding.Addinput.text.toString(),
            karyakarni = binding.Karyainput.text.toString()
        )
        viewModel.addMember(member = data)
    }

    private fun getArguements(){
        family_id = intent.getStringExtra(Constants.FAMILYID).toString()
        binding.IDinput.setText(family_id)
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
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
                    //clear all fields
                    binding.nameinput.text.clear()
                    binding.contactinput.text.clear()
                    binding.Addinput.text.clear()
                    binding.Karyainput.text.clear()
                    binding.DOBinput.text.clear()
                    binding.ageinput.text.clear()
                    binding.genderinput.text.clear()
                    Toast.makeText(this, "Member Added Successfully", Toast.LENGTH_SHORT).show()
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
}
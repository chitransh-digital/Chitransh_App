package com.example.communityapp.ui.family

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
import android.util.Log.e
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private var familyMember="other"
    private var selectedImagePath:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()

        getArguements()


        binding.relationshipSelection1.btnWife.setOnClickListener {
            familyMember = "wife"
            changeUI()
        }

        binding.relationshipSelection1.btnHusband.setOnClickListener {
            familyMember = "husband"
            changeUI()
        }

        binding.relationshipSelection1.btnSon.setOnClickListener {
            familyMember = "son"
            changeUI()
        }

        binding.relationshipSelection1.btnDaughter.setOnClickListener {
            familyMember = "daughter"
            changeUI()
        }

        binding.relationshipSelection1.btnFather.setOnClickListener {
            familyMember = "father"
            changeUI()
        }

        binding.relationshipSelection1.btnMother.setOnClickListener {
            familyMember = "mother"
            changeUI()
        }

        binding.relationshipSelection1.btnOther.setOnClickListener {
            changeUI()
        }

    }

    private fun changeUI(){
        binding.registrationLayout.visibility = View.VISIBLE
        binding.relationshipSelection1.tvMember.visibility = View.GONE
        binding.relationshipSelection1.btnWife.visibility = View.GONE
        binding.relationshipSelection1.btnHusband.visibility = View.GONE
        binding.relationshipSelection1.btnSon.visibility = View.GONE
        binding.relationshipSelection1.btnDaughter.visibility = View.GONE
        binding.relationshipSelection1.btnFather.visibility = View.GONE
        binding.relationshipSelection1.btnMother.visibility = View.GONE
        binding.relationshipSelection1.btnOther.visibility = View.GONE
        registerPageUI()
    }

    private fun registerPageUI(){

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male","Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
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

        val occupationList = arrayListOf("Government Job","Student","Retired","Business","Other")
        if(familyMember =="mother" || familyMember =="wife" || familyMember =="daughter"){
            occupationList.add("HouseWife")
        }

        val occupationadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, occupationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.occupationSpinner.adapter = occupationadapter

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

        //add blood groups in blood group spinners
        val bloodGroupList = arrayListOf("A+","A-","B+","B-","AB+","AB-","O+","O-","other")
        val bloodGroupadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupadapter

        val no = FirebaseAuth.getInstance().currentUser?.phoneNumber

        binding.relationinput.setText(familyMember)

        Log.d("Dashboard phone no",no.toString())

        binding.memberSubmit.setOnClickListener {
            checkDetails()
        }

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
        }

        binding.familyBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivAddImageMember.setOnClickListener {
            openFilePicker()
        }

        binding.occupationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("Spinner" , "$position p $id")
                if (binding.occupationSpinner.selectedItem.toString() == "Business") {
                    binding.familyBusiness.familuBusinessLayout.visibility = View.VISIBLE
                } else {
                    binding.familyBusiness.familuBusinessLayout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.familyBusiness.familuBusinessLayout.visibility = View.GONE
            }
        }

    }

    private fun checkDetails() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(!isValidPhoneNumber(binding.contactinput.text.toString())){
            Toast.makeText(this, "Please enter valid contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.DOBinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your Date of Birth no", Toast.LENGTH_SHORT).show()
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
        else{
            submitRegistration()
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
            occupation = binding.occupationSpinner.selectedItem.toString(),
            bloodGroup = binding.bloodGroupSpinner.selectedItem.toString(),
            profilePic = "NA"
        )
        viewModel.addMember(member = data,selectedImagePath)
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
            binding.ivAddImageMember.setImageURI(uri)
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
}
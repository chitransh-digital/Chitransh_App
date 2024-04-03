package com.example.communityapp.ui.Dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityUpdateMemberBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateMemberActivity : BaseActivity() {


    private lateinit var binding: ActivityUpdateMemberBinding
    private lateinit var member : Member
    private lateinit var viewModel: DashboardViewModel
    private var selectedImagePath:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        if (intent.hasExtra("member")) {
            member = intent.getSerializableExtra("member") as Member
        }
        setObservables()

        binding.nameinput.setText(member.name)
        binding.relationinput.setText(member.relation)
        binding.IDinput.setText(member.familyID)
        binding.contactinput.setText(member.contact)

        binding.landmarkInput.setText(member.address.split(" ").first())

       Glide.with(this).load(member.profilePic).into(binding.profileImage)
        binding.profileImage.setOnClickListener {
            openFilePicker()
        }

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        binding.ageSpinner.setSelection(member.age-1)

        val genderList = arrayListOf("Male", "Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

        var gender =0
        if(member.gender != "Male"){
            gender=1
        }

        binding.genderSpinner.setSelection(
            gender
        )

        val statesList = arrayListOf("Madhya Pradesh")
        val statesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = statesadapter

        //add cities in city spinners
        val citiesList = arrayListOf("Indore", "Bhopal", "Gwalior", "Jabalpur")
        val citiesadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = citiesadapter

        binding.citySpinner.setSelection(0)
        binding.stateSpinner.setSelection(0)
        binding.contactinput.isEnabled = false

        binding.memberUpdate.setOnClickListener {
            val completeAddress= binding.landmarkInput.text.toString() + " " + binding.citySpinner.selectedItem.toString() + " " + binding.stateSpinner.selectedItem.toString()

            val updatedMember = Member(
                name = binding.nameinput.text.toString(),
                relation = binding.relationinput.text.toString(),
                familyID = binding.IDinput.text.toString(),
                contact = binding.contactinput.text.toString(),
                age = binding.ageSpinner.selectedItem.toString().toInt(),
                DOB = member.DOB,
                address = completeAddress,
                bloodGroup = member.bloodGroup,
                education = member.education,
                gender = member.gender,
                profilePic = member.profilePic,
                occupation = member.occupation,
                karyakarni = member.karyakarni,
            )
            viewModel.updateMember(member.contact, updatedMember, selectedImagePath)

        }
    }

    val getImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            selectedImagePath = getImagePath(uri!!).toString()
            binding.profileImage.setImageURI(uri)
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


    private fun setObservables(){
        viewModel.updatedUser.observe(this, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("Success", resources.data.toString())
                    hideProgressDialog()
                    Toast.makeText(this, "Member Updated Successfully", Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    Log.e("Loading", resources.data.toString())
                    showProgressDialog("loading")
                }

                Resource.Status.ERROR -> {
                    showErrorSnackBar(resources.apiError.toString())
                    Log.e("Error", resources.apiError.toString())
                }

                else -> {}
            }
        })
    }
}
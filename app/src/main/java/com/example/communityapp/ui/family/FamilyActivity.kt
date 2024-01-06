package com.example.communityapp.ui.family

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityBusinessBinding
import com.example.communityapp.databinding.ActivityFamilyBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.nio.channels.MembershipKey

@AndroidEntryPoint
class FamilyActivity : AppCompatActivity() {

    private lateinit var viewModel: FamilyViewModel
    private lateinit var binding: ActivityFamilyBinding
    private lateinit var family_id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()

        getArguements()

        val no = FirebaseAuth.getInstance().currentUser?.phoneNumber

        Log.d("Dashboard phoe no",no.toString())

        binding.memberSubmit.setOnClickListener {
            checkDetails()
        }

        binding.dateSelector.setOnClickListener {

        }
    }

    private fun checkDetails() {
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty()){
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
}
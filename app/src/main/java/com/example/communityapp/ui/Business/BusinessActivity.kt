package com.example.communityapp.ui.Business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.Business
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityBusinessBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {

    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var binding: ActivityBusinessBinding
    private lateinit var id : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservables()
        getArguments()

        binding.Submit.setOnClickListener {
            checkFields()
        }

        binding.businessBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun getArguments(){
        id = intent.getStringExtra(Constants.CONTACT).toString()
    }

    private fun setObservables() {
        viewModel.business.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Business Registered", Toast.LENGTH_SHORT).show()
                    binding.nameinput.text?.clear()
                    binding.contactinput.text?.clear()
                    binding.Addinput.text?.clear()
                    binding.descinput.text?.clear()
                    Log.e("B Success",resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    Log.e(" B Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Some Error Occurred! Please try again", Toast.LENGTH_SHORT).show()
                    Log.e("B Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }

    private fun checkFields(){
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.Addinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your address no", Toast.LENGTH_SHORT).show()
        }else if(binding.descinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your description no", Toast.LENGTH_SHORT).show()
        }else{
            submitRegistration()
        }
    }

    private fun submitRegistration(){
        val data = Business(
            name = binding.nameinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            address = binding.Addinput.text.toString(),
            desc = binding.descinput.text.toString(),
            ownerID = id
        )
        viewModel.addBusiness(data)
    }
}
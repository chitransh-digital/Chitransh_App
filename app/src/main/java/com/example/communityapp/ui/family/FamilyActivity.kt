package com.example.communityapp.ui.family

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FamilyActivity : AppCompatActivity() {

    private lateinit var viewModel: FamilyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family)

        viewModel = ViewModelProvider(this)[FamilyViewModel::class.java]

        setObservables()

        viewModel.addMember(
            Member(
                name = "Priya Agarwal",
                gender = "Female",
                age = 48,
                address = "Jaipur, Rajasthan",
                karyakarni = "Maha samaj",
                familyID = "145236987",
                contact = "7737751653"
            )
        )
    }

    private fun setObservables(){
        viewModel.user.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Success",resources.data.toString())
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
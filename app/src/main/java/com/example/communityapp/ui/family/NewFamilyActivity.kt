package com.example.communityapp.ui.family

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityFamilyBinding
import com.example.communityapp.databinding.ActivityNewFamilyBinding
import com.example.communityapp.databinding.FragmentProfileBinding
import com.example.communityapp.ui.Dashboard.profileAdapter
import com.example.communityapp.ui.SignUp.SignUpActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewFamilyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewFamilyBinding
    val viewModel: FamilyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservables()

        binding.baselineSearchIcon.setOnClickListener {
            if(binding.etSearchBar.text.isNotEmpty()){
                viewModel.getMembers(binding.etSearchBar.text.toString())
            }else{
                Toast.makeText(this,"Please enter a family ID",Toast.LENGTH_SHORT).show()
            }
        }

        binding.profileBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setObservables() {
        viewModel.user_data.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    try {
                        if(resources.data.isNullOrEmpty()){
                            Toast.makeText(this,"No family found",Toast.LENGTH_SHORT).show()
                            return@Observer
                        }
                        val user_data = resources.data!!
                        Log.e("D Success",resources.data.toString())
                        setUpRecyclerView(user_data)
                    } catch (e: Exception){
                        Log.e("D Error",e.toString())
                    }
                }
                Resource.Status.LOADING -> {
                    Log.e(" D Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("D Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }

    private fun setUpRecyclerView(data : List<Member>){
        val adapter = profileAdapter(this,data)
        binding.rvOtherFamillyMembers.adapter = adapter
        binding.rvOtherFamillyMembers.layoutManager  = LinearLayoutManager(this)
    }
}
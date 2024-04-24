package com.example.communityapp.ui.family

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
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
class NewFamilyActivity : BaseActivity() {
    private lateinit var binding: ActivityNewFamilyBinding
    val viewModel: FamilyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWindowsUp()
        setObservables()
        showProgressDialog("Fetching Family Details...")
        viewModel.getFamilyByCity()
        binding.baselineSearchIcon.setOnClickListener {
            if(binding.searchTypeSpinner.selectedItem.toString() == "ALL" || binding.etSearchBar.text.isNotEmpty()){
                showProgressDialog("Fetching Family Details...")
                viewModel.getFamilyByCity()
            }else{
                Toast.makeText(this,"Please enter a family ID",Toast.LENGTH_SHORT).show()
            }
        }

        binding.profileBack.setOnClickListener {
            onBackPressed()
        }



        val TypeList = arrayListOf("ALL","Family ID","City")
        val Typeadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TypeList)
        Typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.searchTypeSpinner.adapter = Typeadapter


    }

    private fun setObservables() {
        viewModel.user_data.observe(this, Observer {resources ->
            hideProgressDialog()
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    try {
                        if(resources.data.isNullOrEmpty()){
                            Toast.makeText(this,"No family found",Toast.LENGTH_SHORT).show()
                            return@Observer
                        }
                        val user_data = resources.data
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
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                }
                else -> {}
            }
        })
    }

    private fun setUpRecyclerView(data : List<List<Member>>){
        val result = mutableListOf<List<Member>>()

        if(binding.searchTypeSpinner.selectedItem.toString() == "ALL"){
            for (it in data){
                if(it.isNotEmpty())result.add(it)
            }
        }else if (binding.searchTypeSpinner.selectedItem.toString() == "City"){
            for(it in data){
                if(it.isNotEmpty()){
                    if (it[0].address == binding.etSearchBar.text.toString()){
                        result.add(it)
                    }
                }
            }
        }else if(binding.searchTypeSpinner.selectedItem.toString() == "Family ID"){
            for (it in data){
                if (it.isNotEmpty()){
                    if (it[0].familyID == binding.etSearchBar.text.toString()){
                        result.add(it);
                    }
                }
            }
        }

        Log.e("Result",result.toString())

        val adapter = FamilyAdapter(this,result)
        binding.rvOtherFamillyMembers.adapter = adapter
        binding.rvOtherFamillyMembers.layoutManager  = LinearLayoutManager(this)
    }
}
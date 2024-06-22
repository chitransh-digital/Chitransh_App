package com.example.communityapp.ui.Business

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.newModels.Business
import com.example.communityapp.databinding.ActivityViewBusinessBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewBusinessActivity : BaseActivity() {

    lateinit var binding: ActivityViewBusinessBinding
    private val viewModel: BusinessViewModel by viewModels()
    private var mOriginalBusinessList: MutableList<Business> = mutableListOf()
    private var mFilteredBusinessList: MutableList<Business> = mutableListOf()
    private lateinit var businessAdapter: BusinessAdapter
    private var limit=10
    private var page=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWindowsUp()

        binding.viewBusinessBack.setOnClickListener {
            onBackPressed()
        }

        setupRV()

        setObservales()

        viewModel.getBusiness(limit, page)

        binding.businessSearchIcon.setOnClickListener {
            val query = binding.etSearchBusiness.text.toString()
            filterBusinessList(query)
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Check if the selected position is not the first item
                    if(position!=0){
                        val selectedItem = parent?.getItemAtPosition(position).toString()
                        filterData(selectedItem,binding.spinner3.selectedItem.toString())
                    }
                    // Handle the selection here


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected event if needed
            }
        }

        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Check if the selected position is not the first item
                    if(position!=0){
                        // Handle the selection here
                        val selectedItem = parent?.getItemAtPosition(position).toString()
                        filterData(binding.spinner2.selectedItem.toString(),selectedItem)
                    }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected event if needed
            }
        }
    }

    private fun setObservales(){
        viewModel.business_list.observe(this, Observer {resources ->

            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
//                    mOriginalBusinessList.clear()
                    resources.data?.businesses?.let { mOriginalBusinessList.addAll(it) }
                    businessAdapter.notifyDataSetChanged()
                    Log.e("B Success",resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Fetching Business Details...")
                    Log.e(" B Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError.toString())
                    Log.e("B Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }

        private fun setupRV(){
            businessAdapter = BusinessAdapter(this, mOriginalBusinessList)
            binding.rvBusiness.layoutManager = LinearLayoutManager(this)
            binding.rvBusiness.adapter = businessAdapter
        }
    private fun filterBusinessList(query: String) {
        mFilteredBusinessList.clear()
        if (query.isNotEmpty()) {
            for (business in mOriginalBusinessList) {
                if (business.name.contains(query, ignoreCase = true) || business.city.contains(query, ignoreCase = true) ) {
                    mFilteredBusinessList.add(business)
                }
            }
        } else {
            mFilteredBusinessList.addAll(mOriginalBusinessList)
            binding.spinner2.setSelection(0)
            binding.spinner3
                .setSelection(0)
        }

        if(mFilteredBusinessList.size == 0){
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show()
        }
        val businessAdapter = BusinessAdapter(this, mFilteredBusinessList)
        binding.rvBusiness.layoutManager = LinearLayoutManager(this)
        binding.rvBusiness.adapter = businessAdapter
    }

    fun filterData(type: String, city: String) {
        mFilteredBusinessList.clear()
        if(type =="Type" && city =="City"){
            mFilteredBusinessList = mOriginalBusinessList.toMutableList()
        }
        else if(type =="Type"){
            mFilteredBusinessList = mOriginalBusinessList.filter { item ->
                item.city.contains(city)
            }.toMutableList()
        }
        else if(city =="City"){
            mFilteredBusinessList = mOriginalBusinessList.filter { item ->
                item.type.equals(type)
            }.toMutableList()
        }
        else {
            mFilteredBusinessList = mOriginalBusinessList.filter { item ->
                // Apply filtering logic based on the selected values of type and city
                // For example, if Business is the data model, and type and city are properties of Business:
                item.type == type && item.city.contains(city)
            }.toMutableList()
        }


        // Update your RecyclerView adapter with the filtered list
        if(mFilteredBusinessList.size == 0 && mOriginalBusinessList.size!=0){
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show()
        }
        val businessAdapter = BusinessAdapter(this, mFilteredBusinessList)
        binding.rvBusiness.layoutManager = LinearLayoutManager(this)
        binding.rvBusiness.adapter = businessAdapter
    }
}
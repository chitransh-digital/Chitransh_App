package com.example.communityapp.ui.family

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.newModels.FamilyX
import com.example.communityapp.databinding.ActivityNewFamilyBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class NewFamilyActivity : BaseActivity() {
    private lateinit var binding: ActivityNewFamilyBinding
    val viewModel: FamilyViewModel by viewModels()
    private var limit = 15
    private var page = 1
    private var mOriginalFamilyList: MutableList<FamilyX> = mutableListOf()
    private var mFilteredFamilyList: MutableList<FamilyX> = mutableListOf()
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""
    lateinit var adapter :FamilyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        setWindowsUp()
        setObservables()
        showProgressDialog("Fetching Family Details...")
//        viewModel.getFamilyByCity()
        viewModel.getAllFamily(limit,page)

        binding.viewFamilyBack.setOnClickListener {
            onBackPressed()
        }

        binding.familySearchIcon.setOnClickListener {
            val query = binding.etSearchFamily.text.toString()
            filterFamilyList(query)
        }



        val TypeList = arrayListOf("ALL","Family ID","City")
        val Typeadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, TypeList)
        Typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.searchTypeSpinner.adapter = Typeadapter


    }

    private fun filterFamilyList(query: String) {
        if (query.isEmpty()) {
            setUpRecyclerView(mOriginalFamilyList)
            return
        }

        val filteredList = mOriginalFamilyList.filter {
            //filter by id
            it.familyID.contains(query, ignoreCase = true) ||
                    //filter by city
                    it.members.any { member -> member.city.contains(query, ignoreCase = true) }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()
        }

        setUpRecyclerView(filteredList)
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
//                        setUpRecyclerView(user_data)
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

        viewModel.family.observe(this, Observer {resources ->
            hideProgressDialog()
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    try {
                        if(resources.data?.families.isNullOrEmpty()){
                            Toast.makeText(this,"No family found",Toast.LENGTH_SHORT).show()
                            return@Observer
                        }
                        val user_data = resources.data?.families
                        mOriginalFamilyList.addAll(user_data!!)
                        Log.e("Success",resources.data.toString())
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception){
                        Log.e("Error",e.toString())
                    }
                }
                Resource.Status.LOADING -> {
                    Log.e("Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("Error",resources.apiError.toString())
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                }
                else -> {}
            }
        })
    }

    private fun setUpRecyclerView(data : List<FamilyX>){

        val adapter = FamilyAdapter(this,data)
        binding.rvOtherFamillyMembers.adapter = adapter
        binding.rvOtherFamillyMembers.layoutManager  = LinearLayoutManager(this)
    }

    private fun setUpRecyclerViewPaging(data : List<FamilyX>) {
    adapter = FamilyAdapter(this, data)
    val layoutManager = LinearLayoutManager(this)
    binding.rvOtherFamillyMembers.adapter = adapter
    binding.rvOtherFamillyMembers.layoutManager = layoutManager

    binding.rvOtherFamillyMembers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
                && totalItemCount >= limit) {
                page++
                viewModel.getAllFamily(limit, page)
            }
        }
    })
}

    fun filterData(city: String, state: String) {
        Log.e("FilterData", "Filtering Data... $city $state")
        mFilteredFamilyList.clear()

        // Filter the list based on the state and city
        for (family in mOriginalFamilyList) {
           for(member in family.members){
               if (member.relation=="Head" && member.city.equals(city, ignoreCase = true) && member.state.equals(state, ignoreCase = true)) {
                   mFilteredFamilyList.add(family)
               }
           }
        }

        // Update your RecyclerView adapter with the filtered list
        if (mFilteredFamilyList.isEmpty()) {
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show()
        }
        setUpRecyclerView(mFilteredFamilyList)
    }

    private fun init() {
        setUpRecyclerViewPaging(mOriginalFamilyList)
        stringArrayState = ArrayList()
        stringArrayCity = ArrayList()

        // Set city adapter
        val adapterCity = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayCity)
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapterCity

        // Get state json value from assets folder
        try {
            val obj = JSONObject(loadJSONFromAssetState())
            val m_jArry = obj.getJSONArray("statelist")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val state = jo_inside.getString("State")
                stringArrayState.add(state)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayState)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = adapter

        // State spinner item selected listener with the help of this we get selected value
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                spinnerStateValue = binding.stateSpinner.selectedItem.toString()
                _state = spinnerStateValue
                Log.e("SpinnerStateValue", spinnerStateValue)
                stringArrayCity.clear()
                Log.e("CityArray", _city)

                try {
                    val obj = JSONObject(loadJSONFromAssetCity())
                    val m_jArry = obj.getJSONArray("citylist")

                    for (i in 0 until m_jArry.length()) {
                        val jo_inside = m_jArry.getJSONObject(i)
                        val state = jo_inside.getString("State")
                        var city = ""

                        if (spinnerStateValue.equals(state, ignoreCase = true)) {
                            city = jo_inside.getString("city")
                            stringArrayCity.add(city)
                        }
                    }
                    _city=stringArrayCity[0]

                    if(spinnerStateValue!="Select State") {
                        filterData( _city, _state) // Call the filterData function when city is selected
                    }

                    // Notify adapter city for getting selected value according to state
                    adapterCity.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerCityValue = binding.citySpinner.selectedItem.toString()
                Log.e("SpinnerCityValue", spinnerCityValue)

                _city = spinnerCityValue

                if(spinnerCityValue!="Select City") {
                    filterData(_city, _state) // Call the filterData function when city is selected
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun loadJSONFromAssetState(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("state.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun loadJSONFromAssetCity(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("cityState.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
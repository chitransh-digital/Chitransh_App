package com.example.communityapp.ui.Business

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.newModels.Business
import com.example.communityapp.databinding.ActivityViewBusinessBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class ViewBusinessActivity : BaseActivity() {

    lateinit var binding: ActivityViewBusinessBinding
    private val viewModel: BusinessViewModel by viewModels()
    private var mOriginalBusinessList: MutableList<Business> = mutableListOf()
    private var mFilteredBusinessList: MutableList<Business> = mutableListOf()
    private lateinit var businessAdapter: BusinessAdapter
    private var limit = 10
    private var page = 1
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""
    private var _type: String = ""
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        count=0

        setWindowsUp()

        binding.viewBusinessBack.setOnClickListener {
            onBackPressed()
        }
        init()

        setupRV()

        setObservales()

        viewModel.getBusiness(limit, page)

        binding.businessSearchIcon.setOnClickListener {
            val query = binding.etSearchBusiness.text.toString()
            filterBusinessList(query)
        }


    }private fun setObservables() {
        viewModel.business_list.observe(this, Observer { resources ->
            hideProgressDialog()
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
//                    mOriginalBusinessList.clear()
                    resources.data?.businesses?.let { mOriginalBusinessList.addAll(it) }
                    businessAdapter.notifyDataSetChanged()
                    Log.e("B Success", resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Fetching Business Details...")
                    Log.e(" B Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError.toString())
                    Log.e("B Error", resources.apiError.toString())
                }
                else -> {}
            }
        })
    }

    private fun setupRV() {
        businessAdapter = BusinessAdapter(this, mOriginalBusinessList)
        binding.rvBusiness.layoutManager = LinearLayoutManager(this)
        binding.rvBusiness.adapter = businessAdapter
    }

    fun filterData(type: String, city: String, state: String) {
        Log.e("FilterData", "Filtering Data... $type $city $state")
        mFilteredBusinessList.clear()
        if (city=="Select City" && state=="Select State" && type!="Select Type") {
            for (business in mOriginalBusinessList) {
                if (business.type.contains(type, ignoreCase = true)) {
                    mFilteredBusinessList.add(business)
                }
            }
        }
        else if (city!="Select City" && state!="Select State" && type=="Select Type") {
            for (business in mOriginalBusinessList) {
                if (business.city.contains(city, ignoreCase = true) &&
                    business.state.contains(state, ignoreCase = true)) {
                    mFilteredBusinessList.add(business)
                }
            }
        }
        else {
            for (business in mOriginalBusinessList) {
                if (business.type.contains(type, ignoreCase = true) &&
                    business.city.contains(city, ignoreCase = true) &&
                    business.state.contains(state, ignoreCase = true)) {
                    mFilteredBusinessList.add(business)
                }
            }
        }

        // Update your RecyclerView adapter with the filtered list
        if (mFilteredBusinessList.isEmpty()) {
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show()
        }
        businessAdapter = BusinessAdapter(this, mFilteredBusinessList)
        binding.rvBusiness.layoutManager = LinearLayoutManager(this)
        binding.rvBusiness.adapter = businessAdapter
    }

    private fun init() {
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

                    Log.e("CityArray2", _city)

                    if(spinnerStateValue!="Select State") {
                        filterData(_type, _city, _state) // Call the filterData function when city is selected
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
                    filterData(_type, _city, _state) // Call the filterData function when city is selected
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (binding.typeSpinner.selectedItem.toString()!="Select Type") {
                    _type = binding.typeSpinner.selectedItem.toString()
                    filterData(_type, _city, _state) // Call the filterData function when type is selected
                    Log.e("SpinnerTypeValue", _type)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun filterBusinessList(query: String) {
        mFilteredBusinessList.clear()
        if (query.isNotEmpty()) {
            for (business in mOriginalBusinessList) {
                if (business.name.contains(query, ignoreCase = true)) {
                    mFilteredBusinessList.add(business)
                }
            }
        }

        // Update your RecyclerView adapter with the filtered list
        if (mFilteredBusinessList.isEmpty()) {
            Toast.makeText(this, "no result found", Toast.LENGTH_SHORT).show()
        }
        businessAdapter = BusinessAdapter(this, mFilteredBusinessList)
        binding.rvBusiness.layoutManager = LinearLayoutManager(this)
        binding.rvBusiness.adapter = businessAdapter
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

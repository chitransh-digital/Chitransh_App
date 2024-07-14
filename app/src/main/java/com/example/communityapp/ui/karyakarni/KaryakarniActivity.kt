package com.example.communityapp.ui.karyakarni

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
import com.example.communityapp.R
import com.example.communityapp.data.newModels.Karyakarni
import com.example.communityapp.databinding.ActivityKaryakarniBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class KaryakarniActivity : BaseActivity() {

    private lateinit var binding: ActivityKaryakarniBinding
    private val viewModel: KaryakarniViewModel by viewModels()
    private var limit = 10
    private var page = 1
    private var isLoading = false
    private var hasMoreItems = true
    private var mOriginalKaryakarniList: MutableList<Karyakarni> = mutableListOf()
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""
    private lateinit var adapter: KaryaKarniAdapter
    private val updateAbleKaryakarnilist: MutableList<Karyakarni> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaryakarniBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setObservables()

        binding.karyakarniBackBack.setOnClickListener {
            onBackPressed()
        }

        binding.buttonIndia.setOnClickListener {
            binding.stateSpinner.visibility = View.GONE
            binding.citySpinner.visibility = View.GONE
            filterKaryakarni("India")
        }

        binding.buttonState.setOnClickListener {
            binding.stateSpinner.visibility = View.VISIBLE
            binding.citySpinner.visibility = View.GONE
            filterKaryakarni("State")
        }

        binding.buttonCity.setOnClickListener {
            binding.stateSpinner.visibility = View.VISIBLE
            binding.citySpinner.visibility = View.VISIBLE
            filterKaryakarni("City")
        }

        setUpRecyclerViewPaging()
    }

    private fun setObservables() {
        viewModel.karyakarni_list.observe(this, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    resources.data?.let {
                        if (it.karyakarni.isNotEmpty()) {
                            mOriginalKaryakarniList.addAll(it.karyakarni)
                            sortAndNotifyAdapter(mOriginalKaryakarniList)
                            isLoading = false
                            if (it.karyakarni.size < limit) {
                                hasMoreItems = false
                            }
                        } else {
                            hasMoreItems = false
                            showToast(getString(R.string.no_karyakarni_found))
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError?.message.toString())
                    isLoading = false
                }
                Resource.Status.LOADING -> {
                    showProgressDialog(getString(R.string.fetching_karyakarni_details))
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllKaryakarni(limit, page)
    }

    private fun setUpRecyclerViewPaging() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.karyakarniRecycler.layoutManager = layoutManager
        adapter = KaryaKarniAdapter(updateAbleKaryakarnilist)
        binding.karyakarniRecycler.adapter = adapter

        binding.karyakarniRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && !isLoading && hasMoreItems) {
                    isLoading = true
                    page++
                    viewModel.getAllKaryakarni(limit, page)
                }
            }
        })
    }

    private fun sortAndNotifyAdapter(list: List<Karyakarni>) {
        val sortedList = sortKaryakarniList(list)
        updateAbleKaryakarnilist.clear()
        updateAbleKaryakarnilist.addAll(sortedList)
        adapter.notifyDataSetChanged()
    }

    private fun filterKaryakarni(level: String) {
        val filteredList = mOriginalKaryakarniList.filter { it.level == level }

        if (filteredList.isEmpty()) {
            showToast(getString(R.string.no_karyakarni_found))
        }
        sortAndNotifyAdapter(filteredList)
    }

    private fun filterData(state: String, city: String = "") {
        Log.e("FilterData", "Filtering Data... State: $state, City: $city")

        val filteredList = if (city.isEmpty()) {
            // Filter only by state if city is empty
            mOriginalKaryakarniList.filter { it.state.equals(state, ignoreCase = true) && it.level.equals("State", ignoreCase = true)}
        } else {
            // Filter by both city and state
            mOriginalKaryakarniList.filter {
                it.city.equals(city, ignoreCase = true) && it.state.equals(state, ignoreCase = true) &&
                        it.level.equals("City", ignoreCase = true)
            }
        }

        if (filteredList.isEmpty()) {
            showToast(getString(R.string.no_result_found))
        }
        sortAndNotifyAdapter(filteredList)
    }


    private fun getLevelPriority(level: String): Int {
        return when (level) {
            "India" -> 0
            "State" -> 1
            "City" -> 2
            else -> 3
        }
    }

    private fun sortKaryakarniList(karyakarniList: List<Karyakarni>): List<Karyakarni> {
        return karyakarniList.sortedBy { getLevelPriority(it.level) }
    }

    private fun init() {
        stringArrayState = ArrayList()
        stringArrayCity = ArrayList()

        val adapterCity = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayCity)
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapterCity

        try {
            val obj = JSONObject(loadJSONFromAssetState())
            val stateArray = obj.getJSONArray("statelist")

            for (i in 0 until stateArray.length()) {
                val stateObject = stateArray.getJSONObject(i)
                val state = stateObject.getString("State")
                stringArrayState.add(state)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val adapterState = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayState)
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = adapterState

        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerStateValue = binding.stateSpinner.selectedItem.toString()
                _state = spinnerStateValue
                stringArrayCity.clear()

                try {
                    val obj = JSONObject(loadJSONFromAssetCity())
                    val cityArray = obj.getJSONArray("citylist")

                    for (i in 0 until cityArray.length()) {
                        val cityObject = cityArray.getJSONObject(i)
                        val state = cityObject.getString("State")
                        if (spinnerStateValue.equals(state, ignoreCase = true)) {
                            val city = cityObject.getString("city")
                            stringArrayCity.add(city)
                        }
                    }
                    _city = if (stringArrayCity.isNotEmpty()) stringArrayCity[0] else ""

                    if(_state != "Select State")filterData(_state)
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
                _city = spinnerCityValue
                if (spinnerCityValue != "Select City") {
                    filterData(_state, _city)
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

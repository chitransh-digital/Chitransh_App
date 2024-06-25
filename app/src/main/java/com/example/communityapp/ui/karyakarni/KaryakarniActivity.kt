package com.example.communityapp.ui.karyakarni

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.newModels.KaryaMember
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
    private var limit = 100
    private var page = 1
    private var mOriginalKaryakarniList: MutableList<Karyakarni> = mutableListOf()
    private var mFilteredKaryakarniList: MutableList<Karyakarni> = mutableListOf()
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""
    private lateinit var adapter: ExpandableListAdapter
    private var isLoading = false


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
    }

    private fun setObservables() {
        viewModel.karyakarni_list.observe(this, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    resources.data?.let {
                        if (it.karyakarni.isNotEmpty()) {
                            mOriginalKaryakarniList.addAll(it.karyakarni)
                            setUpExpandableView(mOriginalKaryakarniList)
                            isLoading = false // Reset loading flag
                        } else {
                            showToast("No Karyakarni Found")
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError?.message.toString())
                    isLoading = false // Reset loading flag
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Fetching Karyakarni Details...")
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        viewModel.getAllKaryakarni(limit, page)
    }

    private fun setUpExpandableView(karyakarniList: List<Karyakarni>) {
        val sortedList = sortKaryakarniList(karyakarniList)

        val listDataHeader = sortedList
        val listDataChild: MutableMap<Karyakarni, List<KaryaMember>> = mutableMapOf()

        for (karya in sortedList) {
            listDataChild[karya] = karya.members
        }

        adapter = ExpandableListAdapter(this, listDataHeader, listDataChild)
        binding.expandableListView.setAdapter(adapter)
        if(sortedList.isNotEmpty())binding.expandableListView.expandGroup(0)
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

    private fun filterKaryakarni(level: String) {
        val filteredList = mOriginalKaryakarniList.filter { it.level.equals(level, ignoreCase = true) }

        if (filteredList.isEmpty()) {
            showToast("No Karyakarni Found")
        }

        setUpExpandableView(filteredList)
    }

    private fun filterData(city: String, state: String) {
        Log.e("FilterData", "Filtering Data... $city $state")
        mFilteredKaryakarniList.clear()

        if (city == "Select City" || state == "Select State") {
            setUpExpandableView(mOriginalKaryakarniList)
            return
        }

        if (mFilteredKaryakarniList.isEmpty()) {
            showToast("No result found")
        }
        setUpExpandableView(mFilteredKaryakarniList)
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
                    _city = stringArrayCity[0]

                    filterData(_city, _state)
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
                    filterData(_city, _state)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        setUpRecyclerViewPaging()
    }

//    private fun setUpRecyclerViewPaging() {
//        binding.expandableListView.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
//
//            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
//                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount && isLoading.not()) {
//                    // User has scrolled to the bottom
//                    isLoading = true
//                    page++
//                    viewModel.getAllKaryakarni(limit, page)
//                }
//            }
//        })
//    }


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

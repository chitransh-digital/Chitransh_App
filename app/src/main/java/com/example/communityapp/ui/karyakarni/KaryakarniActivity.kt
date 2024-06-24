package com.example.communityapp.ui.karyakarni

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.BaseActivity
import com.example.communityapp.data.newModels.KaryaMember
import com.example.communityapp.data.newModels.Karyakarni
import com.example.communityapp.databinding.ActivityKaryakarniBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KaryakarniActivity : BaseActivity() {

    private lateinit var binding: ActivityKaryakarniBinding
    private val viewModel: KaryakarniViewModel by viewModels()
    private var limit=10
    private var page=1
    private val shortAnimationDuration = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaryakarniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setObservables()

        binding.karyakarniBackBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setObservables() {
        viewModel.karyakarni_list.observe(this, Observer { resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    resources.data?.let {
                        if(it.karyakarni.isNotEmpty()){
                            setUpExpandableView(it.karyakarni)
                        }else{
                            showToast("No Karyakarni Found")
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError?.message.toString())
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Fetching Karyakarni Details...")
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllKaryakarni(limit,page)
    }

    fun setUpExpandableView(Unkaryakarni: List<Karyakarni>) {

        val karyakarni = sortKaryakarniList(Unkaryakarni)

        val listDataHeader = karyakarni

        val listDataChild : MutableMap<Karyakarni, List<KaryaMember>> = mutableMapOf<Karyakarni,List<KaryaMember>>()
        Log.e("Karyakarni", karyakarni.toString())
        for (karya in karyakarni) {
            if (karya.members.isNotEmpty()) {
                listDataChild[karya] = karya.members
            } else {
                listDataChild[karya] = listOf()
            }
        }

        val expandableListAdapter = ExpandableListAdapter(this, listDataHeader, listDataChild)
        binding.expandableListView.setAdapter(expandableListAdapter)

        binding.expandableListView.expandGroup(0)
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
}
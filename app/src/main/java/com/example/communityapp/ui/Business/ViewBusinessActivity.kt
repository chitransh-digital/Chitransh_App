package com.example.communityapp.ui.Business

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityViewBusinessBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewBusinessActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewBusinessBinding
    private val viewModel: BusinessViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.viewBusinessBack.setOnClickListener {
            OnBackPressedDispatcher().onBackPressed()
        }

        setObservales()
        viewModel.getBusiness()
    }

    private fun setObservales(){
        viewModel.business_list.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    val businessAdapter = BusinessAdapter(this, resources.data!!)
                    binding.rvBusiness.layoutManager = LinearLayoutManager(this)
                    binding.rvBusiness.adapter = businessAdapter
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
}
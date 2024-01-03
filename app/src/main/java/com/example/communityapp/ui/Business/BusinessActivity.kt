package com.example.communityapp.ui.Business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.communityapp.R
import com.example.communityapp.data.models.Business
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {

    private val viewModel: BusinessViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)

        setObservables()

        viewModel.addBusiness(
            Business(
                name = "Hare Krihna Store",
                ownerID = "6378228784",
                desc = "Sab Kuch Milega",
                contact = "8756932140",
                address = "Shyam ji ke bagal me, Tanki ke neeche"
            )
        )
    }

    private fun setObservables() {
        viewModel.business.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("B Success",resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    Log.e(" B Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("B Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }
}
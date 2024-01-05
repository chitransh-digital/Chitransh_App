package com.example.communityapp.ui.Dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityDashboardBinding
import com.example.communityapp.utils.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: DashboardViewModel
    var bottomNavigationView: BottomNavigationView? = null
    private lateinit var binding : ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        setObservables()
        setUpNavigation()
//        viewModel.getMember("6378228784")
    }

    private fun setUpNavigation() {
        val bottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.Frag) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setObservables() {
        viewModel.user_data.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("D Success",resources.data.toString())
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
}
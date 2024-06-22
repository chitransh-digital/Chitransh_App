package com.example.communityapp.ui.Dashboard

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.PreferencesHelper
import com.example.communityapp.databinding.ActivityDashboardBinding
import com.example.communityapp.ui.SignUp.SignUpActivity
import com.example.communityapp.ui.auth.Login_activity
import com.example.communityapp.ui.karyakarni.KaryakarniActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var viewModel: DashboardViewModel
    private var phoneNum : String = ""
    private lateinit var binding : ActivityDashboardBinding


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setObservables()
        setUpNavigation()
//        setDialog()

        phoneNum = preferencesHelper.getContact().toString()
        Log.d("Dashboard token no",preferencesHelper.getToken().toString())
//        phoneNum = intent.getStringExtra(Constants.USERNAME).toString()
        Log.d("Dashboard phone no",phoneNum)

        binding.navDrawerView.setNavigationItemSelectedListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast

            Log.e("FCM token", token)
        })

        setWindowsUp()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                            MaterialAlertDialogBuilder(this)
                                .setTitle("Permission Required")
                                .setMessage("Permission is required to send notifications")
                                .setPositiveButton("Grant") { dialog, which ->
                                    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                                .setNegativeButton("Cancel") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }


    private fun setUpNavigation() {
        val bottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.Frag) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        binding.toolbarDashboardActivity.setNavigationIcon(R.drawable.hamburger_menu)
        binding.toolbarDashboardActivity.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer((GravityCompat.START))
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setObservables() {
        viewModel.user_data.observe(this, Observer {resources ->

            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    Log.e("D Success",resources.data.toString())
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Please wait...")
                    Log.e(" D Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    Log.e("D Error",resources.apiError.toString())
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                    if(resources.apiError?.message == Constants.Error404)startSignUpActivity()
                }
                else -> {}
            }
        })

        viewModel.loginStatusPhone.observe(this, Observer {resources ->

            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    preferencesHelper.setToken(resources.data?.token.toString())
                    Log.e("D Success",resources.data.toString())
                    viewModel.getMember(phoneNum)
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Please wait...")
                    Log.e(" D Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    Log.e("D Error",resources.apiError.toString())
                    showErrorSnackBar("Error: ${resources.apiError?.message}")
                    if(resources.apiError?.message == Constants.Error404)startSignUpActivity()
                }
                else -> {}
            }
        })
    }

    private fun startSignUpActivity() {
        val intent = Intent(this,SignUpActivity::class.java)
        Log.d("Dashboard phone no",intent.toString())
        startActivity(intent)
        Toast.makeText(this, R.string.please_SignUp, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Onresume","Data fetching")
        Log.d("Onresume phone num","Data fetching $phoneNum")
        if (phoneNum.isNotEmpty()) {

            Log.d("Onresume phone num","Data fetching $phoneNum")
            viewModel.getMember(phoneNum)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_myprofile->{
                supportFragmentManager.beginTransaction().replace(R.id.Frag,ProfileFragment())
                    .addToBackStack(Constants.HOME_FRAG).commit()
            }

            R.id.nav_signout->{
                preferencesHelper.clear()
                startActivity(Intent(this,Login_activity::class.java))
                finish()
            }

            R.id.karyakrni -> {
                startActivity(Intent(this, KaryakarniActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
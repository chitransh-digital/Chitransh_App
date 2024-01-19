package com.example.communityapp.ui.Dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityDashboardBinding
import com.example.communityapp.ui.SignUp.SignUpActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.FirebaseFCMService
import com.example.communityapp.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var phoneNum : String
    private lateinit var binding : ActivityDashboardBinding
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        setObservables()
        setUpNavigation()

        val id = FirebaseAuth.getInstance().currentUser?.phoneNumber
        phoneNum = id.toString()
        Log.d("Dashboard phone no",id.toString())

        if (id != null) {
            viewModel.getMember(id)
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

        if (!runBlocking { isSubscribedToTopic() }) {
            // Perform subscription only if not already subscribed
            subscribeToTopic()
        }
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
                    var user_data = resources.data!!
                    Log.e("D Success",resources.data.toString())
                    if(user_data.isEmpty()){
                        Toast.makeText(this, R.string.please_SignUp, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,SignUpActivity::class.java)
                        intent.putExtra(Constants.PHONE_NUM,phoneNum)
                        startActivity(intent)
                        finish()
                    }
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

    private  fun subscribeToTopic() {
        // Call the suspend function within a coroutine
        FirebaseMessaging.getInstance().subscribeToTopic("notify")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Subscription successful, set the flag to indicate subscription
                    lifecycleScope.launch(Dispatchers.Main) {
                        setSubscriptionFlag()
                    }
                    val msg = "Subscribed to topic"
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                } else {
                    val msg = "Subscribe to topic failed: ${task.exception?.message}"
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun isSubscribedToTopic(): Boolean {
        val preferencesKey = booleanPreferencesKey("isSubscribed")
        return dataStore.data.first()[preferencesKey] ?: false
    }

    private suspend fun setSubscriptionFlag() {
        val preferencesKey = booleanPreferencesKey("isSubscribed")
        dataStore.edit { settings ->
            settings[preferencesKey] = true
        }
    }

}
package com.example.communityapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.ui.jobs.JobsActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Login_activity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =ViewModelProvider(this)[LoginViewModel::class.java]

        binding.button.setOnClickListener {
            val ph = "+91" + binding.editTextPhone.text.toString()
            if(ph.isNullOrEmpty()){
                Toast.makeText(this, "Input your phone number", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.OnVerificationCodeSent(ph,this)
            }
        }

    }

    fun setObservables(){
        viewModel.verification.observe(this, Observer {resouce->
            when(resouce.status){
                Resource.Status.SUCCESS->{
                    Log.e("url",resouce.status.toString())
                    startActivity(Intent(this, JobsActivity::class.java))
                    finish()
                }
                Resource.Status.ERROR->{
                    Log.e("url",resouce.status.toString())
                }
                Resource.Status.LOADING->{
                    Log.e("url","loading")
                }
                else -> {
                    Log.e("url","else")
                }
            }

        })



    }
}
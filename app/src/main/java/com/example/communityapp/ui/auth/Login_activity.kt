package com.example.communityapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Login_activity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var verificationID : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =ViewModelProvider(this)[LoginViewModel::class.java]

        binding.button.setOnClickListener {
            val ph = "+91" + binding.editTextPhone.text.toString()
            if(ph.isEmpty()){
                Toast.makeText(this, "Input your phone number", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.OnVerificationCodeSent(ph,this)
            }
        }

        binding.buttonotp.setOnClickListener {
            val otp = binding.editTextPhone.text.toString()
            if(otp.isEmpty()){
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show()
            }else{
                val credential = PhoneAuthProvider.getCredential(verificationID, otp)
                viewModel.signInWithPhoneAuthCredential(credential,this)
            }
        }

    }

    fun setObservables(){
        viewModel.verification.observe(this, Observer {resouce->
            when(resouce.status){
                Resource.Status.SUCCESS->{
                    Log.e("url",resouce.status.toString())
                    codesent(resouce.data!!)
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

    private fun codesent(data: String) {
        verificationID = data
        binding.text.text = "Please enter the OTP"
        binding.editTextPhone.text.clear()
        binding.button.visibility = View.GONE
        binding.buttonotp.visibility = View.VISIBLE
    }
}
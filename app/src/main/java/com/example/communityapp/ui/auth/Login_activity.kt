package com.example.communityapp.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.ui.family.FamilyActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Login_activity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var verificationID: String
    private var contentPointer = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val welcomeText = getString(R.string.welcome_text)
        val spannableString = SpannableString(welcomeText)

        // Set color for "Chitransh Digital"
        val colorSpan = ForegroundColorSpan(Color.parseColor("#620402"))
        spannableString.setSpan(
            colorSpan,
            welcomeText.indexOf("Chitransh Digital"),
            welcomeText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.preLoginTextView.text = spannableString

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setObservables()

        contentPointer = 1
        showContent(contentPointer)

        Handler().postDelayed({

            var currentUserID = FirebaseAuth.getInstance().currentUser

            if (currentUserID != null) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                contentPointer++
                showContent(contentPointer)
            }
//            finish()
        }, 2000)

        binding.buttonEnglish.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
        }

        binding.buttonHindi.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
        }

        binding.buttonProceedPhno.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
        }

        binding.buttonPhoneNo.setOnClickListener {
            val ph = "+91" + binding.editTextPhone.text.toString()
            if (ph.isEmpty()) {
                Toast.makeText(this, "Input your phone number", Toast.LENGTH_SHORT).show()
            } else {
//                viewModel.OnVerificationCodeSent(ph, this)
                codeSent("good")
            }
        }

        binding.buttonOTP.setOnClickListener {
            val otp = binding.editTextPhone.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(verificationID, otp)
                viewModel.signInWithPhoneAuthCredential(credential, this)
            }
        }

    }

    private fun setObservables() {
        viewModel.verificationStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("url", resource.status.toString())
                    if (resource.data?.first == 1) {
                        codeSent(resource.data.second)
                    } else {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                }

                Resource.Status.ERROR -> {
                    Log.e("url", resource.status.toString())
                }

                Resource.Status.LOADING -> {
                    Log.e("url", "loading")

                }

                else -> {
                    Log.e("url", "else")
                }
            }

        })


    }

    private fun codeSent(data: String) {
        verificationID = data
        contentPointer++
        showContent(contentPointer)
    }

    private fun showContent(pointer: Int) {
        when (pointer) {
            1 -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.preLoginTextView.visibility = View.GONE
                binding.buttonProceedPhno.visibility = View.GONE
                binding.preLoginLangSelect.visibility = View.GONE
                binding.preLoginLangSelect.visibility = View.GONE
                binding.buttonHindi.visibility = View.GONE
                binding.buttonEnglish.visibility = View.GONE
                binding.loginOtpText.visibility = View.GONE
                binding.editTextPhone.visibility = View.GONE
                binding.buttonPhoneNo.visibility = View.GONE
                binding.buttonOTP.visibility = View.GONE
            }

            2 -> {
                binding.progressBar.visibility = View.GONE
                binding.preLoginTextView.visibility = View.GONE
                binding.buttonProceedPhno.visibility = View.GONE
                binding.preLoginLangSelect.visibility = View.VISIBLE
                binding.buttonHindi.visibility = View.VISIBLE
                binding.buttonEnglish.visibility = View.VISIBLE
                binding.loginOtpText.visibility = View.GONE
                binding.editTextPhone.visibility = View.GONE
                binding.buttonPhoneNo.visibility = View.GONE
                binding.buttonOTP.visibility = View.GONE
            }

            3 -> {
                binding.progressBar.visibility = View.GONE
                binding.preLoginTextView.visibility = View.VISIBLE
                binding.buttonProceedPhno.visibility = View.VISIBLE
                binding.preLoginLangSelect.visibility = View.GONE
                binding.buttonHindi.visibility = View.GONE
                binding.buttonEnglish.visibility = View.GONE
                binding.loginOtpText.visibility = View.GONE
                binding.editTextPhone.visibility = View.GONE
                binding.buttonPhoneNo.visibility = View.GONE
                binding.buttonOTP.visibility = View.GONE
            }

            4 -> {
                binding.progressBar.visibility = View.GONE
                binding.preLoginTextView.visibility = View.GONE
                binding.buttonProceedPhno.visibility = View.GONE
                binding.preLoginLangSelect.visibility = View.GONE
                binding.buttonHindi.visibility = View.GONE
                binding.buttonEnglish.visibility = View.GONE
                binding.loginOtpText.visibility = View.VISIBLE
                binding.editTextPhone.visibility = View.VISIBLE
                binding.buttonPhoneNo.visibility = View.VISIBLE
                binding.buttonOTP.visibility = View.GONE
            }

            5 -> {
                binding.progressBar.visibility = View.GONE
                binding.preLoginTextView.visibility = View.GONE
                binding.buttonProceedPhno.visibility = View.GONE
                binding.preLoginLangSelect.visibility = View.GONE
                binding.buttonHindi.visibility = View.GONE
                binding.buttonEnglish.visibility = View.GONE
                binding.loginOtpText.visibility = View.VISIBLE
                binding.editTextPhone.visibility = View.VISIBLE
                binding.buttonPhoneNo.visibility = View.GONE
                binding.buttonOTP.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        contentPointer--
        when(contentPointer){
            0,1->{
                super.onBackPressed()
            }
            else->{
                showContent(contentPointer)
            }
        }
    }
}
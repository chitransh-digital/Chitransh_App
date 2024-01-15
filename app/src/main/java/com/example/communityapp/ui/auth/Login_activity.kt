package com.example.communityapp.ui.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.ActivityOptions
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
import com.example.communityapp.data.models.FamilyData
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.ui.SignUp.SignUpActivity
import com.example.communityapp.ui.family.FamilyActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.example.communityapp.utils.moveAndResizeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Login_activity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var verificationID: String
    private var contentPointer = 1
    private var shortAnimationDuration = 500
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

        var currentUserID = FirebaseAuth.getInstance().currentUser
        val PhNO = FirebaseAuth.getInstance().currentUser?.phoneNumber
        Handler().postDelayed({
            if (currentUserID != null && PhNO != null) {
                viewModel.getMember(PhNO)
                finish()
            } else {
                moveAndResizeView(binding.logoImage, -200f, (binding.logoImage.height / 1.2).toInt())
                contentPointer++
                showContent(contentPointer)
            }
        },2000)


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
                checkPerson(FamilyData(emptyList()))
//                val credential = PhoneAuthProvider.getCredential(verificationID, otp)
//                viewModel.signInWithPhoneAuthCredential(credential, this)
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

        viewModel.user_data.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    var user_data = resources.data!!
                    checkPerson(user_data)
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

    private fun checkPerson(userData: FamilyData) {
        if(userData.data.isEmpty()){
            val intent = Intent(this, SignUpActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this, binding.logoImage, getString(R.string.transition_name)).toBundle()
            startActivity(intent, options)
        }else{
            val intent = Intent(this,DashboardActivity::class.java)
            intent.putExtra(Constants.USER_DATA,userData)
            startActivity(intent)
        }
    }

    private fun codeSent(data: String) {
        verificationID = data
        contentPointer++
        showContent(contentPointer)
    }

    private fun showContent(pointer: Int) {
        when (pointer) {
            1 -> {
                crossFade(
                    listOf(binding.progressBar),
                    listOf(
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP
                    )
                )
            }

            2 -> {
                crossFade(
                    listOf(binding.preLoginLangSelect, binding.buttonHindi, binding.buttonEnglish),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP
                    )
                )
            }

            3 -> {
                crossFade(
                    listOf(binding.preLoginTextView, binding.buttonProceedPhno),
                    listOf(
                        binding.progressBar,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP
                    )
                )
            }

            4 -> {
                binding.loginOtpText.text = getString(R.string.enter_phno)
                binding.editTextPhone.hint = getString(R.string.enter_phno)
                crossFade(
                    listOf(binding.loginOtpText, binding.editTextPhone, binding.buttonPhoneNo),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.buttonOTP
                    )
                )
            }

            5 -> {
                binding.loginOtpText.text = getString(R.string.enter_otp)
                binding.editTextPhone.hint = getString(R.string.enter_otp)
                crossFade(
                    listOf(binding.loginOtpText, binding.editTextPhone, binding.buttonOTP),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.buttonPhoneNo
                    )
                )
            }
        }
    }


    override fun onBackPressed() {
        contentPointer--
        when (contentPointer) {
            0, 1 -> {
                super.onBackPressed()
            }

            else -> {
                showContent(contentPointer)
            }
        }
    }

    private fun crossFade(visible: List<View>, invisible: List<View>) {

        for (view in visible) {
            view.apply {
                // Set the content view to 0% opacity but visible, so that it is
                // visible but fully transparent during the animation.
                alpha = 0f
                visibility = View.VISIBLE
                // Animate the content view to 100% opacity and clear any animation
                // listener set on the view.
                animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }

        for (view in invisible) {
            view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.progressBar.visibility = View.GONE
                    }
                })
        }
    }
}
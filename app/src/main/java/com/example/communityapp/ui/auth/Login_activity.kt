package com.example.communityapp.ui.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.ui.Dashboard.DashboardActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.example.communityapp.utils.moveAndResizeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class Login_activity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var verificationID: String
    private var contentPointer = 1
    private var shortAnimationDuration = 500
    private var contact = ""

    //    var context: Context? = null
//    var resources: Resources? = null
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

//        val phNo = FirebaseAuth.getInstance().currentUser?.phoneNumber
        val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
        val phNo = sharedPreferences.getString(Constants.PHONE_NUMBER, null)
        Log.e("Login Activity", " Answer it $phNo")


        Handler().postDelayed({
            if (!phNo.isNullOrEmpty() && phNo != "NA") {
                val intent = Intent(this, DashboardActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    binding.logoImage,
                    getString(R.string.transition_name)
                ).toBundle()
                startActivity(intent, options)
                finish()
            } else {
                moveAndResizeView(
                    binding.logoImage,
                    -200f,
                    (binding.logoImage.height / 1.2).toInt()
                )
                contentPointer++
                showContent(contentPointer)
            }
        }, 2000)



        binding.buttonEnglish.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
//            context = LocaleHelper.setLocale(this, "en");
//            resources = context!!.resources;
//            setLocal(this@Login_activity, "en")
        }

        binding.buttonHindi.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
//            context = LocaleHelper.setLocale(this, "hi");
//            resources = context!!.resources;
//            setLocal(this@Login_activity, "hi")
        }

        binding.buttonProceedPhno.setOnClickListener {
            contentPointer++
            showContent(contentPointer)
        }

        binding.buttonPhoneNo.setOnClickListener {
            if(binding.editTextPhone.text.toString().length != 10){
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }else {
                val ph = "+91" + binding.editTextPhone.text.toString()
                contact = ph
                if (ph.isEmpty()) {
                    Toast.makeText(this, "Input your phone number", Toast.LENGTH_SHORT).show()
                } else {

                    viewModel.OnVerificationCodeSent(ph, this)
                }
            }
        }

        binding.buttonOTP.setOnClickListener {
            val otp = binding.editTextPhone.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show()
            } else {
                showProgressDialog("Verifying OTP..")
                val credential = PhoneAuthProvider.getCredential(verificationID, otp)
                viewModel.signInWithPhoneAuthCredential(credential, this)
            }
        }

        binding.loginViaFamilyID.setOnClickListener {
            contentPointer = 6
            showContent(contentPointer)
        }

        binding.buttonProceedFamilyID.setOnClickListener {
            contentPointer = 6
            showContent(contentPointer)
        }

        binding.loginViaOtp.setOnClickListener {
            contentPointer = 4
            showContent(contentPointer)
        }

        binding.buttonLoginUsernameSubmit.setOnClickListener {
            val username = "+91"+binding.editTextUsername.text.toString()
            contact = username
            val familyID = binding.editTextFamilyID.text.toString()
            if (username.isEmpty() || familyID.isEmpty()) {
                Toast.makeText(this, "Please enter username and family ID", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.d("LoginActivity", "Username: $username, FamilyID: $familyID")
                showProgressDialog("Verifying Family ID..")
                viewModel.signInWithUsername(username, familyID)
            }
        }

        setWindowsUp()

    }

    private fun setLocal(activity: Activity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = activity.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        activity.createConfigurationContext(configuration)
    }

    private fun setObservables() {
        viewModel.verificationStatus.observe(this, Observer { resource ->

            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    Log.e("url", resource.status.toString())
                    if (resource.data?.first == 1) {
                        codeSent(resource.data.second)
                    } else {
                        // shared pref update
                        val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString(Constants.PHONE_NUMBER, contact)
                        editor.apply()
                        hideProgressDialog()

                        // start DashboardActivity after otp verification
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra(Constants.USERNAME, contact)
                        val options = ActivityOptions.makeSceneTransitionAnimation(
                            this,
                            binding.logoImage,
                            getString(R.string.transition_name)
                        ).toBundle()
                        startActivity(intent, options)
                    }
                }

                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    Log.e("url", resource.status.toString())
                    showErrorSnackBar("Error: ${resource.apiError?.message}")
                }

                Resource.Status.LOADING -> {
                    showProgressDialog("Sending Otp..")
                    Log.e("url", "loading")

                }

                else -> {
                    Log.e("url", "else")
                }
            }

        })

        viewModel.loginStatus.observe(this, Observer { resource ->
            hideProgressDialog()
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("url", resource.status.toString())
                    //shared pref update
                    val sharedPreferences = getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Constants.PHONE_NUMBER, contact)
                    editor.apply()
                    hideProgressDialog()

                    //start dashboard activty after family id verfiication
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra(Constants.USERNAME, contact)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        binding.logoImage,
                        getString(R.string.transition_name)
                    ).toBundle()
                    startActivity(intent, options)
                }

                Resource.Status.ERROR -> {
                    Log.e("url Error", "what is it " + resource.apiError)
                    binding.editTextUsername.setText("")
                    binding.editTextFamilyID.setText("")
                    showErrorSnackBar("Error: ${resource.apiError?.message}")
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
        binding.editTextPhone.setText("")
        showContent(contentPointer)
        hideProgressDialog()
        Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show()
    }

    private fun showContent(pointer: Int) {
        Log.d("LoginActivity", "Pointer: $pointer")
        when (pointer) {
            1 -> {
                crossFade(
                    listOf(binding.progressBar),
                    listOf(
                        binding.preLoginTextView,
                        binding.preLoginLangSelect,
                        binding.buttonProceedPhno,
                        binding.buttonProceedFamilyID,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP,
                        binding.loginViaFamilyID,
                        binding.loginUsernameText,
                        binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
                    )
                )
            }

            2 -> {
                crossFade(
                    listOf(binding.preLoginLangSelect, binding.buttonHindi, binding.buttonEnglish),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedFamilyID,
                        binding.buttonProceedPhno,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP,
                        binding.loginViaFamilyID,
                        binding.loginUsernameText,
                        binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
                    )
                )
            }

            3 -> {
                crossFade(
                    listOf(
                        binding.preLoginTextView, binding.buttonProceedPhno,
                        binding.buttonProceedFamilyID
                    ),
                    listOf(
                        binding.progressBar,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP,
                        binding.loginViaFamilyID,
                        binding.loginUsernameText,
                        binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
                    )
                )
            }

            4 -> {
                binding.loginOtpText.text = getString(R.string.enter_phno)
                binding.editTextPhone.hint = getString(R.string.enter_phno)
                crossFade(
                    listOf(
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.loginViaFamilyID,
                        binding.buttonPhoneNo
                    ),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.buttonProceedFamilyID,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.buttonOTP,
                        binding.loginUsernameText,
                        binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
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
                        binding.buttonProceedFamilyID,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.buttonPhoneNo,
                        binding.loginViaFamilyID,
                        binding.loginUsernameText,
                        binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
                    )
                )
            }

            6 -> {
                crossFade(
                    listOf(
                        binding.loginUsernameText, binding.editTextUsername,
                        binding.editTextFamilyID,
                        binding.buttonLoginUsernameSubmit,
                        binding.loginViaOtp
                    ),
                    listOf(
                        binding.progressBar,
                        binding.preLoginTextView,
                        binding.buttonProceedPhno,
                        binding.buttonProceedFamilyID,
                        binding.preLoginLangSelect,
                        binding.buttonHindi,
                        binding.buttonEnglish,
                        binding.loginOtpText,
                        binding.editTextPhone,
                        binding.buttonPhoneNo,
                        binding.buttonOTP,
                        binding.loginViaFamilyID,
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

            5 -> {
                contentPointer = 3
                showContent(contentPointer)
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

            view.apply {
                alpha = 0f
                visibility = View.GONE

                animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

//            view.animate()
//                .alpha(0f)
//                .setDuration(shortAnimationDuration.toLong())
//                .setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        binding.progressBar.visibility = View.GONE
//                    }
//                })
        }
    }
}
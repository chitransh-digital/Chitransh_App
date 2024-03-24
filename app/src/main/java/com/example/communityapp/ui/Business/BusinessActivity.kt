package com.example.communityapp.ui.Business

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.R
import com.example.communityapp.data.models.Business
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.ActivityBusinessBinding
import com.example.communityapp.ui.Dashboard.ProfileFragment
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {

    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var binding: ActivityBusinessBinding
    private lateinit var id : String
    private var shortAnimationDuration = 500
    private val PICK_IMAGES_REQUEST = 1
    private lateinit var imageAdapter: ImageAdapter
    private val imagesList: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservables()
        getArguments()

        imageAdapter = ImageAdapter(imagesList)
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = imageAdapter

        binding.Submit.setOnClickListener {
            checkFields()
        }

        binding.businessBack.setOnClickListener {
            onBackPressed()
        }
        binding.addImageButton.setOnClickListener {
            if(imagesList.size<4){
                selectImages()
            }else{
                Toast.makeText(this, "at most 4 images can be uploaded", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun selectImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    // Add the image URI to the list
                    imagesList.add(imageUri.toString())
                }
            } else if (data?.data != null) {
                val imageUri: Uri = data.data!!
                // Add the image URI to the list
                imagesList.add(imageUri.toString())
            }
            // Notify adapter about data changes
            imageAdapter.notifyDataSetChanged()
        }
    }



    private fun getArguments(){
        id = intent.getStringExtra(Constants.CONTACT).toString()

        val businessTypeList = arrayListOf("Restaurant", "Retail Store", "Tech", "Consulting Firm", "other")
        val businessTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, businessTypeList)
        businessTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.businessSpinner.adapter=businessTypeAdapter
    }

    private fun setObservables() {
        viewModel.business.observe(this, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Business Registered", Toast.LENGTH_SHORT).show()
                    binding.nameinput.text?.clear()
                    binding.contactinput.text?.clear()
                    binding.Addinput.text?.clear()
                    binding.descinput.text?.clear()
                    binding.linkInput.text?.clear()
                    imagesList.clear()
                    imageAdapter.notifyDataSetChanged()
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

    private fun checkFields(){
        if (binding.nameinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(binding.contactinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        }else if(binding.Addinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your address no", Toast.LENGTH_SHORT).show()
        }else if(binding.descinput.text.isNullOrEmpty()){
            Toast.makeText(this, "Please enter your description no", Toast.LENGTH_SHORT).show()
        }else{
            submitRegistration()
        }
    }

    private fun submitRegistration(){
        var businessLink="NA"
        if(binding.linkInput.text.isNotEmpty()){
            businessLink=binding.linkInput.text.toString()
        }
        val data = Business(
            name = binding.nameinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            address = binding.Addinput.text.toString(),
            desc = binding.descinput.text.toString(),
            ownerID = id,
            type = binding.businessSpinner.selectedItem.toString(),
            link = businessLink,
            images = emptyList(),
            coupon = "NA"
        )
        viewModel.addBusiness(data, imagesList)
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
                    }
                })
        }
    }
}
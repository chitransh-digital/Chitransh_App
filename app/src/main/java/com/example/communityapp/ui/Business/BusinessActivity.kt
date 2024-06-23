package com.example.communityapp.ui.Business

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
import com.example.communityapp.databinding.ActivityBusinessBinding
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class BusinessActivity : BaseActivity() {

    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var binding: ActivityBusinessBinding
    private lateinit var id: String
    private var shortAnimationDuration = 500
    private val PICK_IMAGES_REQUEST = 1
    private val FILE_PICK_REQUEST_CODE = 2
    private lateinit var imageAdapter: ImageAdapter
    private val imagesList: MutableList<String> = ArrayList()
    private val multiPartList: MutableList<MultipartBody.Part> = ArrayList()
    private var mFileURI: Uri? = null
    private var multiPartFile: MultipartBody.Part? = null
    private lateinit var stringArrayState: ArrayList<String>
    private lateinit var stringArrayCity: ArrayList<String>
    private var spinnerStateValue: String = ""
    private var _city: String = ""
    private var _state: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservables()
        getArguments()
        init()

        imageAdapter = ImageAdapter(imagesList,multiPartList)
        binding.imageRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = imageAdapter

        binding.Submit.setOnClickListener {
            checkFields()
        }

        binding.businessBack.setOnClickListener {
            onBackPressed()
        }
        binding.addImageButton.setOnClickListener {
            if (imagesList.size < 4) {
                selectImages()
            } else {
                Toast.makeText(this, "at most 4 images can be uploaded", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addFileButton.setOnClickListener {
            val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            filePickerIntent.type = "*/*" // Allow any file type to be selected
            startActivityForResult(filePickerIntent, FILE_PICK_REQUEST_CODE)
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
                    val selectedImagePath = getImagePath(imageUri).toString()
                        val file = File(selectedImagePath)
                        Log.e("ImageFile", file.path)

                        val requestBody = file.asRequestBody(contentResolver.getType(imageUri)?.toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        multiPartList.add(body)

                    // Add the image URI to the list
                    imagesList.add(imageUri.toString())
                }
            } else if (data?.data != null) {
                val imageUri: Uri = data.data!!
                val selectedImagePath = getImagePath(imageUri).toString()
                val file = File(selectedImagePath)
                Log.e("ImageFile", file.path)
                val requestBody = file.asRequestBody(contentResolver.getType(imageUri)?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                multiPartList.add(body)
                // Add the image URI to the list
                imagesList.add(imageUri.toString())
            }
            // Notify adapter about data changes
            imageAdapter.notifyDataSetChanged()
        }

        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val fileUri = data?.data
            // Handle the selected file URI, for example:
            mFileURI=fileUri
            fileUri?.let { uri ->
                val resolver = contentResolver
                val displayName = getFileName(uri, resolver)
                displayName?.let {
                    binding.fileText.text = it
                    binding.fileText.visibility = View.VISIBLE
                }

                val filePath = getFileFromUri(uri)
                Log.e("File URI", uri.toString())
                Log.e("File Path", filePath.toString())
                if (filePath != null) {
                    val requestBody = filePath.asRequestBody(resolver.getType(uri)?.toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", filePath.name, requestBody)
                    multiPartFile = body
                }
            }
        }
    }

    private fun getImagePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    fun getFileName(uri: Uri, resolver: ContentResolver): String? {
        var name: String? = null
        val cursor = resolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    fun getFileFromUri(uri: Uri): File? {
        val documentFile = DocumentFile.fromSingleUri(this, uri)
        documentFile?.let {
            val file = File(cacheDir, it.name ?: "temp_file")
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
            return file
        }
        return null
    }

    fun copyStream(input: InputStream, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }


    private fun getArguments() {
        id = intent.getStringExtra(Constants.CONTACT).toString()

        val businessTypeList =
            arrayListOf("Restaurant", "Retail Store", "Tech", "Consulting Firm", "Other")
        val businessTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, businessTypeList)
        businessTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.businessSpinner.adapter = businessTypeAdapter
    }

    private fun setObservables() {
        viewModel.business.observe(this, Observer { resources ->
            hideProgressDialog()
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Business Registered", Toast.LENGTH_SHORT).show()
                    binding.nameinput.text?.clear()
                    binding.contactinput.text?.clear()
                    binding.Addinput.text?.clear()
                    binding.descinput.text?.clear()
                    binding.linkInput.text?.clear()
                    imagesList.clear()
                    imageAdapter.notifyDataSetChanged()
                    Log.e("B Success", resources.data.toString())
                }

                Resource.Status.LOADING -> {
                    Log.e(" B Loading", resources.data.toString())
                }

                Resource.Status.ERROR -> {
                    showErrorSnackBar("Some error occurred please try again later")
                    Log.e("B Error", resources.apiError.toString())
                }

                else -> {}
            }
        })
        setWindowsUp()
    }

    private fun checkFields() {
        if (binding.nameinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else if (binding.contactinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your contact no", Toast.LENGTH_SHORT).show()
        } else if (binding.Addinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your address no", Toast.LENGTH_SHORT).show()
        } else if (binding.descinput.text.isNullOrEmpty()) {
            Toast.makeText(this, "Please enter your description no", Toast.LENGTH_SHORT).show()
        } else {
            submitRegistration()
        }
    }

    private fun submitRegistration() {

        var businessLink = "NA"
        if (binding.linkInput.text.isNotEmpty()) {
            businessLink = binding.linkInput.text.toString()
        }
        val data = com.example.communityapp.data.newModels.Business(
            name = binding.nameinput.text.toString(),
            contact = binding.contactinput.text.toString(),
            city = _city,
            state = _state,
            landmark = binding.Addinput.text.toString(),
            desc = binding.descinput.text.toString(),
            ownerID = id,
            type = binding.businessSpinner.selectedItem.toString(),
            link = businessLink,
            images = emptyList(),
            coupon = "none",
            attachments = emptyList(),
            id = id
        )
        showProgressDialog("Registering Business...")
        viewModel.addBusiness(data, multiPartList, multiPartFile)
    }

    private fun loadJSONFromAssetState(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("state.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun loadJSONFromAssetCity(): String? {
        var json: String? = null
        try {
            val iss: InputStream = applicationContext.assets.open("cityState.json")
            val size = iss.available()
            val buffer = ByteArray(size)
            iss.read(buffer)
            iss.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun init() {
        stringArrayState = ArrayList()
        stringArrayCity = ArrayList()

        // Set city adapter
        val adapterCity = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayCity)
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.citySpinner.adapter = adapterCity

        // Get state json value from assets folder
        try {
            val obj = JSONObject(loadJSONFromAssetState())
            val m_jArry = obj.getJSONArray("statelist")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)
                val state = jo_inside.getString("State")
                stringArrayState.add(state)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, stringArrayState)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateSpinner.adapter = adapter

        // State spinner item selected listener with the help of this we get selected value
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position)
                val text = binding.stateSpinner.selectedItem.toString()

                spinnerStateValue = binding.stateSpinner.selectedItem.toString()
                _state = spinnerStateValue
                Log.e("SpinnerStateValue", spinnerStateValue)
                stringArrayCity.clear()

                try {
                    val obj = JSONObject(loadJSONFromAssetCity())
                    val m_jArry = obj.getJSONArray("citylist")

                    for (i in 0 until m_jArry.length()) {
                        val jo_inside = m_jArry.getJSONObject(i)
                        val state = jo_inside.getString("State")

                        if (spinnerStateValue.equals(state, ignoreCase = true)) {
                            _city = jo_inside.getString("city")
                            stringArrayCity.add(_city)
                        }
                    }

                    // Notify adapter city for getting selected value according to state
                    adapterCity.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerCityValue = binding.citySpinner.selectedItem.toString()
                Log.e("SpinnerCityValue", spinnerCityValue)

                _city = spinnerCityValue
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }



}
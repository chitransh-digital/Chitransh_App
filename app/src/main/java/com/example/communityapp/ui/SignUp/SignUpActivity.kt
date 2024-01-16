package com.example.communityapp.ui.SignUp

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivitySignUpBinding
import java.util.Locale

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private var selectedDate: Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ageList = (1..100).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ageSpinner.adapter = adapter

        val genderList = arrayListOf("Male","Female")
        val genadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genadapter

        binding.dateSelector.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate.set(year, month, dayOfMonth)
                updateSelectedDateText()
                updateAgeFromDOB()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Optional: Set a maximum date

        datePickerDialog.show()
    }

    private fun updateSelectedDateText() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        binding.DOBinput.setText(formattedDate)
    }

    private fun updateAgeFromDOB() {
        val currentDate = Calendar.getInstance()
        var age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)

        // Optionally, you can use the selected month and day to refine the age calculation
        if (currentDate.get(Calendar.MONTH) < selectedDate.get(Calendar.MONTH) ||
            (currentDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                    currentDate.get(Calendar.DAY_OF_MONTH) < selectedDate.get(Calendar.DAY_OF_MONTH))) {
            // Subtract 1 year if the birth date hasn't occurred yet this year
            age--
        }

        // Set the calculated age to the Spinner
        binding.ageSpinner.setSelection(age - 1) // Subtract 1 since age starts from 1
    }
}
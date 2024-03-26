package com.example.communityapp.ui.Dashboard
//
//import android.app.Dialog
//import android.content.Context
//import android.view.View
//import android.widget.RadioGroup
//import com.example.communityapp.R
//
//class Relation_info_dialog(context: Context, private val onOptionSelected: (Int) -> Unit) : Dialog(context, R.style.BlurredBackground) {
//
//    private lateinit var blurView: View
//
//    init {
//        setContentView(R.layout.relation_info_dialog)
//
//        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
//        blurView = findViewById(R.id.blurView)  // Get reference to blur view
//
//        // Handle radio button selection
//        radioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val selectedId = radioGroup.indexOfChild(findViewById(checkedId))
//            onOptionSelected(selectedId)
//            dismiss() // Dismiss dialog on selection
//        }
//
//        // Prevent dismissal by outside touch
//        setCancelable(false)
//
//        // Optional: Show blur view on dialog show (consider performance impact)
//        fun onShow() {  // No override keyword needed here
//            super.onShow()
//            blurView.visibility = View.VISIBLE
//        }
//
//        // Optional: Hide blur view on dialog dismiss
//        fun onDismiss() {  // No override keyword needed here
//            super.onDismiss()
//            blurView.visibility = View.GONE
//        }
//    }
//}

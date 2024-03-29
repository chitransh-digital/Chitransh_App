package com.example.communityapp

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private  lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

    }

    fun showProgressDialog(text : String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun showErrorSnackBar(message  :String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message,Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    fun setWindowsUp(){
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

}
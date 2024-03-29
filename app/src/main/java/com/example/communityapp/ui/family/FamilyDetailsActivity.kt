package com.example.communityapp.ui.family

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidworrkshop.ui.FamilyDetailsAdapter
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.allMembers
import com.example.communityapp.databinding.ActivityFamilyDetailsBinding
import com.example.communityapp.databinding.ActivityNewFamilyBinding
import com.example.communityapp.ui.Dashboard.profileAdapter
import com.example.communityapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FamilyDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityFamilyDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var member_data = getIntent().getSerializableExtra(Constants.FAMILYDATA) as allMembers
        setWindowsUp()
        Log.e("FamilyDetailsActivity",member_data.toString())

        setUpRv(member_data.allMembers)

        binding.familyBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRv(member_data : List<Member>){
        val adapter = FamilyDetailsAdapter(this,member_data)
        binding.memberRv.adapter = adapter
        binding.memberRv.layoutManager  = GridLayoutManager(this,2)
    }

}
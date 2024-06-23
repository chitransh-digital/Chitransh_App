package com.example.communityapp.ui.family

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
    private var screepointer = 0
    private var shortAnimationDuration = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeUI(screepointer)
        var member_data = getIntent().getSerializableExtra(Constants.FAMILYDATA) as allMembers
        setWindowsUp()
        Log.e("FamilyDetailsActivity",member_data.toString())

        setUpRv(member_data.allMembers)

        binding.familyBack.setOnClickListener {
            screepointer--
            changeUI(screepointer)
        }
    }

    private fun setUpRv(member_data : List<Member>){
        val adapter = FamilyDetailsAdapter(this,member_data, object :
            FamilyDetailsAdapter.onClickListener {
            override fun onClick(model: Member) {
                screepointer++
                changeUI(screepointer)
                populateUI(model)
                Log.d("FamilyDetailsActivity",model.toString())
            }
        })
        binding.memberRv.adapter = adapter
        binding.memberRv.layoutManager  = GridLayoutManager(this,2)
    }

    private fun populateUI(member: Member) {
        binding.previewNameinput.text = member.name
        binding.previewContactinput.text = member.contact
        binding.previewDOBtext.text = member.DOB
        binding.previewAgeSpinner.text = member.age.toString()
        binding.previewGenderSpinner.text = member.gender
        binding.previewRelationInput.text = member.relation
        binding.previewFamilyIDinput.text = member.familyID
        binding.previewLandmarkInput.text = member.address
        binding.previewBloodGroupSpinner.text = member.bloodGroup
        binding.previewKaryainput.text = member.karyakarni
        binding.previewOccuLevelSpinner.text = member.occupation
        binding.previewEduLevelSpinner.text = member.highestEducation
        binding.previewEduDepartInput.text = member.branch
        binding.previewEduInstituteInput.text = member.institute
        binding.previewEduAdditionalInput.text = member.additionalDetails
        binding.previewOccuEmployerInput.text = member.employer
        binding.previewOccuDepartmentInput.text = member.department
        binding.previewOccuAddressInput.text = member.location
        binding.previewOccuPositioninput.text = member.post
        binding.previewEduCourseInput.text = member.course
        Glide.with(this)
            .load(member.profilePic).into(binding.previewIvAddImageMember)

        if (member.occupation == "Business") {
            binding.previewOccuDepartment.visibility = View.GONE
            binding.previewOccuEmployer.visibility = View.GONE
            binding.previewOccuPosition.visibility = View.GONE
            binding.previewOccuAddress.visibility = View.GONE
            binding.previewBuisType.visibility = View.VISIBLE
            binding.previewBuisName.visibility = View.VISIBLE
            binding.previewBuisAddress.visibility = View.VISIBLE
            binding.previewBuisTypeInput.text = member.buisType
            binding.previewBuisNameInput.text = member.buisName
            binding.previewBuisAddressInput.text = member.location
        } else {

            binding.previewOccuDepartment.visibility = View.VISIBLE
            binding.previewOccuEmployer.visibility = View.VISIBLE
            binding.previewOccuPosition.visibility = View.VISIBLE
            binding.previewOccuAddress.visibility = View.VISIBLE
            binding.previewBuisType.visibility = View.GONE
            binding.previewBuisName.visibility = View.GONE
            binding.previewBuisAddress.visibility = View.GONE
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
        }
    }

    private fun changeUI(screenPointer: Int) {
        when (screenPointer) {
            -1 -> onBackPressed()

            0 -> {
                crossFade(
                    listOf(binding.memberRv),
                    listOf(
                        binding.informationPreviewPage
                    )
                )
            }

            1 -> {
                crossFade(
                    listOf(binding.informationPreviewPage),
                    listOf(
                        binding.memberRv
                    )
                )
            }

            else -> {}
        }
    }

}
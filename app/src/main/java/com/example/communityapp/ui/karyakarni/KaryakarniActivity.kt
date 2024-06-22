package com.example.communityapp.ui.karyakarni

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.newModels.KaryaMember
import com.example.communityapp.databinding.ActivityKaryakarniBinding
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KaryakarniActivity : BaseActivity() {

    private lateinit var binding: ActivityKaryakarniBinding
    private val viewModel: KaryakarniViewModel by viewModels()
    private var limit=10
    private var page=1
    private var contentPointer = 1
    private val shortAnimationDuration = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaryakarniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showContent(contentPointer)
        setObservables()

        binding.karyakarniBackBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setObservables() {
        viewModel.karyakarni_list.observe(this, Observer { resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    hideProgressDialog()
                    resources.data?.let {
                        if(it.karyakarni.isNotEmpty()){
                            binding.rvKaryakarni.layoutManager = LinearLayoutManager(this)
                            binding.rvKaryakarni.adapter = KaryaKarniAdapter(it.karyakarni,
                                object : KaryaKarniAdapter.onClickListener {
                                    override fun onClick(member: List<KaryaMember>) {
                                        setUpRV(member)
                                    }
                                }
                            )
                        }else{
                            showToast("No Karyakarni Found")
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    hideProgressDialog()
                    showErrorSnackBar(resources.apiError?.message.toString())
                }
                Resource.Status.LOADING -> {
                    showProgressDialog("Fetching Karyakarni Details...")
                }

            }
        })
    }

    private fun setUpRV(karyaMembers: List<KaryaMember>) {
        binding.rvKaryakarniMember.layoutManager = LinearLayoutManager(this)
        binding.rvKaryakarniMember.adapter = KaryakarniMemberAdapter(karyaMembers)
        contentPointer++
        showContent(contentPointer)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllJobs(limit,page)
    }

    private fun showContent(contentPointer : Int){
        when(contentPointer){
            1 -> {
                crossFade(listOf(binding.rvKaryakarni), listOf(binding.rvKaryakarniMember))
            }
            2 -> {
                crossFade(listOf(binding.rvKaryakarniMember), listOf(binding.rvKaryakarni))
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
                visibility = View.INVISIBLE

                animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }
    }

    override fun onBackPressed() {
        contentPointer -= 1
        Log.e("KaryaKarni Activity" , "$contentPointer")
        if (contentPointer == 1) {
            showContent(contentPointer)
        } else {
            super.onBackPressed()
        }
    }
}
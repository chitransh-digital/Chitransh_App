package com.example.communityapp.ui.Dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.FragmentHomeBinding
import com.example.communityapp.ui.Business.BusinessActivity
import com.example.communityapp.ui.family.FamilyActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var user_data : Member
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        setObservables()

        binding.card3.setOnClickListener {
            val intent = Intent(requireContext(),BusinessActivity::class.java)
            intent.putExtra(Constants.UUID,user_data.uuid)
            startActivity(intent)
        }

        binding.card1.setOnClickListener {
            val intent = Intent(requireContext(),FamilyActivity::class.java)
            intent.putExtra(Constants.FAMILYID,user_data.familyID)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setObservables() {
        viewModel.user_data.observe(viewLifecycleOwner, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Profile Success",resources.data.toString())
                    updateUI(resources.data!!)
                }
                Resource.Status.LOADING -> {
                    Log.e(" Profile Loading",resources.data.toString())
                }
                Resource.Status.ERROR -> {
                    Log.e("Profile Error",resources.apiError.toString())
                }
                else -> {}
            }
        })
    }

    private fun updateUI(data : List<Member>){
        for(ip in data){
            if (ip.contact == "7737751653"){
                user_data = ip
                binding.topGreeting.text = "Namaskar ${ip.name} Ji"
                break
            }
        }

    }

}
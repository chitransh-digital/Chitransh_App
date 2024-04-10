package com.example.communityapp.ui.Dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.FragmentProfileBinding
import com.example.communityapp.ui.auth.Login_activity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: DashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        setObservables()

        binding.profileBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.logoutButton.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear().apply()
            startActivity(Intent(requireContext(), Login_activity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }

    private fun setObservables() {
        viewModel.user_data.observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("Profile Success", resources.data.toString())
                    setUpRecyclerView(resources.data!!, resources.data[0].relation)
                    binding.familyIDShow.text = "Family ID : ${resources.data.get(0).familyID}"
                }

                Resource.Status.LOADING -> {
                    Log.e(" Profile Loading", resources.data.toString())
                }

                Resource.Status.ERROR -> {
                    Log.e("Profile Error", resources.apiError.toString())
                }

                else -> {}
            }
        })

        viewModel.deleteUser.observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Resource.Status.SUCCESS -> {
                    Log.e("Profile Success", resources.data.toString())
                    Toast.makeText(context, "Member deleted", Toast.LENGTH_SHORT).show()
                }

                Resource.Status.LOADING -> {
                    Log.e(" Profile Loading", resources.data.toString())
                }

                Resource.Status.ERROR -> {
                    Log.e("Profile Error", resources.apiError.toString())
                }

                else -> {}
            }
        })
    }

    private fun setUpRecyclerView(data: List<Member>, relation: String) {
        val members = arrayListOf<Member>()
        for(mem in data){
            members.add(mem)
        }
        for(i in 0 until members.size){
            if(members[i].relation == "HEAD"){
                val temp = members[0]
                members[0] = members[i]
                members[i] = temp
            }
        }
        val adapter =
            profileAdapter(requireContext(), data, object : profileAdapter.onClickListener {
                override fun onClick(member: Member) {
//                    viewModel.deleteMember(member.familyID, member.contact)
                }
            })
        binding.rvMembers.adapter = adapter
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
    }

}
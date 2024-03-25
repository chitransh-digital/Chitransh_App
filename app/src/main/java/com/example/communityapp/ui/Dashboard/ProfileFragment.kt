package com.example.communityapp.ui.Dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.databinding.FragmentProfileBinding
import com.example.communityapp.ui.auth.Login_activity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

        binding.logoutButton.setOnClickListener{
            val sharedPreferences = requireActivity().getSharedPreferences(Constants.LOGIN_FILE, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.PHONE_NUM, "NA")
            editor.apply()
            startActivity(Intent(requireContext(),Login_activity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }

    private fun setObservables() {
        viewModel.user_data.observe(viewLifecycleOwner, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("Profile Success",resources.data.toString())
                    setUpRecyclerView(resources.data!!)
                    binding.familyIDShow.text = "Family ID : ${resources.data.get(0).familyID}"
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

    private fun setUpRecyclerView(data : List<Member>){
        val adapter = profileAdapter(requireContext(),data)
        binding.rvMembers.adapter = adapter
        binding.rvMembers.layoutManager  =LinearLayoutManager(requireContext())
    }

}
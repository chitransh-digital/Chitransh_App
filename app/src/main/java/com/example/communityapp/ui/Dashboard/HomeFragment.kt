package com.example.communityapp.ui.Dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.databinding.FragmentHomeNewBinding
import com.example.communityapp.ui.Business.BusinessActivity
import com.example.communityapp.ui.family.FamilyActivity
import com.example.communityapp.ui.jobPosting.JobPostingActivity
import com.example.communityapp.ui.jobs.JobsActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeNewBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var user_data : Member
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeNewBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        setObservables()

        binding.card3.setOnClickListener {
            val intent = Intent(requireContext(),BusinessActivity::class.java)
            intent.putExtra(Constants.CONTACT,user_data.contact)
            startActivity(intent)
        }

        binding.card1.setOnClickListener {
            val intent = Intent(requireContext(),FamilyActivity::class.java)
            intent.putExtra(Constants.FAMILYID,user_data.familyID)
            startActivity(intent)
        }

        binding.card2.setOnClickListener {
            val intent = Intent(requireContext(),JobPostingActivity::class.java)
            startActivity(intent)
        }

        binding.card4.setOnClickListener {
            val intent = Intent(requireContext(),JobsActivity::class.java)
            startActivity(intent)
        }

        binding.ivProfile.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.Frag,ProfileFragment())
                .addToBackStack(Constants.HOME_FRAG).commit()
        }

        return binding.root
    }

    private fun setObservables() {
        viewModel.user_data.observe(viewLifecycleOwner, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("home Success",resources.data.toString())
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

        viewModel.feeds.observe(viewLifecycleOwner, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("news load Success",resources.data.toString())
                    try {
                        setupRv(resources.data!!)
//                        val newsBylocation = resources.data.filter { it.location in user_data.address }
//                        setupRvbyLocation(newsBylocation)
                        setupRvbyLocation(resources.data)
                    }
                    catch (e : Exception){
                        Log.e("news load error",e.toString())
                    }
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
            val id = FirebaseAuth.getInstance().currentUser?.phoneNumber
            if (ip.contact == id){
                user_data = ip
                viewModel.getFeedsByPaging()
//                binding.topGreeting.text = "Namaskar ${ip.name} Ji"
                break
            }
        }

    }

    private fun setupRv(newsList: List<NewsFeed>){
        val adapter = SmallNewsAdapter(requireContext(),newsList){ position, newsList ->
            // Handle the item click, and pass the newsList to another fragment

            val bundle = Bundle().apply {
                putInt("position", position)
                putParcelableArrayList("newsFeedList", ArrayList(newsList))
            }

            val anotherFragment = News()
            anotherFragment.arguments = bundle

            // Replace the current fragment with AnotherFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.Frag, anotherFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvRecentNews.adapter = adapter

        binding.rvRecentNews.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,false)
    }
    private fun setupRvbyLocation(newsBylocation: List<NewsFeed>) {

        val adapter = SmallNewsAdapter(requireContext(),newsBylocation){ position, newsList ->
            // Handle the item click, and pass the newsList to another fragment

            val bundle = Bundle().apply {
                putInt("position", position)
                putParcelableArrayList("newsFeedList", ArrayList(newsList))
            }

            val anotherFragment = News()
            anotherFragment.arguments = bundle

            // Replace the current fragment with AnotherFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.Frag, anotherFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvLocalNews.adapter = adapter

        binding.rvLocalNews.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,false)
    }


}
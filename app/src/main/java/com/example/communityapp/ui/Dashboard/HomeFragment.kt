package com.example.communityapp.ui.Dashboard

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.data.PreferencesHelper
import com.example.communityapp.data.models.Member
import com.example.communityapp.data.models.NewsFeed
import com.example.communityapp.data.newModels.MemberX
import com.example.communityapp.databinding.FragmentHomeNewBinding
import com.example.communityapp.databinding.RelationInfoDialogBinding
import com.example.communityapp.ui.Business.BusinessActivity
import com.example.communityapp.ui.Business.ViewBusinessActivity
import com.example.communityapp.ui.family.FamilyActivity
import com.example.communityapp.ui.family.NewFamilyActivity
import com.example.communityapp.ui.jobs.JobsActivity
import com.example.communityapp.utils.Constants
import com.example.communityapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint

class HomeFragment : Fragment(){
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: FragmentHomeNewBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var user_data : MemberX
    private var uniqueRelations: List<String>? = null
    private var contact = ""
    private var headAddress = ""
    private var familyId= ""
    private val limit =10
    private var page = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeNewBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]
        setObservables()


        contact = preferencesHelper.getContact().toString()

        binding.card2.setOnClickListener {
            val intent = Intent(requireContext(),BusinessActivity::class.java)
            intent.putExtra(Constants.CONTACT,user_data.contact)
            startActivity(intent)
        }

        binding.card1.setOnClickListener {
            val intent = Intent(requireContext(),FamilyActivity::class.java)
            intent.putExtra(Constants.FAMILYID,familyId)
            intent.putStringArrayListExtra(Constants.UNIQUE_RELATIONS, ArrayList(uniqueRelations))
            intent.putExtra(Constants.HEAD_ADDRESS,headAddress)
            Log.d("Head Address",headAddress)
            startActivity(intent)
        }

        binding.card4.setOnClickListener {
            try {
                val intent = Intent(requireContext(),JobsActivity::class.java)
                intent.putExtra(Constants.NAME,user_data.name)
                startActivity(intent)
            }catch (e:Exception){
                Log.e("error",e.toString())
            }
        }

        binding.card6.setOnClickListener{
            val intent = Intent(requireContext(),ViewBusinessActivity::class.java)
            startActivity(intent)
        }

        binding.card5.setOnClickListener {
            val intent = Intent(requireContext(),NewFamilyActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setObservables() {
        viewModel.user_data.observe(viewLifecycleOwner, Observer {resources ->
            when(resources.status){
                Resource.Status.SUCCESS -> {
                    Log.e("home Success",resources.data.toString())
                    uniqueRelations =
                        resources.data!!.families[0].members .distinctBy { it.relation }.map { it.relation }
                    updateUI(resources.data.families[0].members)
                    familyId= resources.data.families[0].familyID
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
                        setupRv(resources.data!!.Feeds)
                        setupRvbyLocation(resources.data.Feeds)
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

    private fun updateUI(data : List<MemberX>){
        val id = contact
        for(ip in data){
            if(ip.relation == "Head"){
                headAddress = ip.landmark + ip.city + ip.state
            }
            if (ip.contact == id){
                user_data = ip
                break
            }
        }

        viewModel.getFeedsByPaging(limit,page)
//        setDialog(data)
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
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,false)
    }

    private fun setDialog(
        user_data: List<Member>
    ) {
        if (user_data.isEmpty()){
            return
        }



        val dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.relation_info_dialog)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        window?.setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg))

        dialog.setCancelable(false) // Make dialog non-cancelable

        dialog.show()  // Add this line to show the dialog

        var bind = RelationInfoDialogBinding.inflate(layoutInflater)

        for (user in user_data){
            Log.d("Relation",user.relation.uppercase())
            if (user.relation.uppercase() == "WIFE"){
                activity?.findViewById<Button>(R.id.qWife)?.visibility = View.VISIBLE
                Log.d("Wife","Wife")
            }
            if (user.relation.uppercase() == "HEAD"){
                bind.qHead.visibility = View.VISIBLE
                Log.d("Head","Head")
            }
            if (user.relation.uppercase() == "HUSBAND") {
                activity?.findViewById<Button>(R.id.qHusband)?.visibility = View.VISIBLE
                Log.d("Husband","Husband")
            }
            if (user.relation.uppercase() == "SON") {
                activity?.findViewById<Button>(R.id.qSon)?.visibility = View.VISIBLE
                Log.d("Son","Son")
            }
            if (user.relation.uppercase() == "DAUGHTER") {
                activity?.findViewById<Button>(R.id.qDaughter)?.visibility = View.VISIBLE
                Log.d("Daughter","Daughter")
            }
            if (user.relation.uppercase() == "FATHER") {
                activity?.findViewById<Button>(R.id.qFather)?.visibility = View.VISIBLE
                Log.d("Father","Father")
            }
            if (user.relation.uppercase() == "MOTHER") {
                activity?.findViewById<Button>(R.id.qMother)?.visibility = View.VISIBLE
                Log.d("Mother","Mother")
            }
            if (user.relation.uppercase() == "OTHER") {
                activity?.findViewById<Button>(R.id.qOther)?.visibility = View.VISIBLE
                Log.d("Other","Other")
            }
        }

    }

}
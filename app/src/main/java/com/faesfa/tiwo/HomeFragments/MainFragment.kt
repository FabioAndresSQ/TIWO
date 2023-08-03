package com.faesfa.tiwo.HomeFragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.faesfa.tiwo.CreateActivity
import com.faesfa.tiwo.DataManager
import com.faesfa.tiwo.InfoActivity
import com.faesfa.tiwo.QuickActivity
import com.faesfa.tiwo.R
import com.faesfa.tiwo.Workouts
import com.faesfa.tiwo.WorkoutsAdapter
import com.faesfa.tiwo.WorkoutsModelClass
import com.faesfa.tiwo.databinding.FragmentMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), WorkoutsAdapter.OnItemClickListener {

    @Inject lateinit var dataManager : DataManager

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var workouts : Workouts
    private lateinit var jsonString : String
    private var showingOpts = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root //save inflater to view

        checkIfBtnAreShowing()

        jsonString = try {
            dataManager.getJsonFromFile(context)!!
        } catch (e : Exception){ //if File is Empty or null Create it
            val createJsonString = "{\"workouts\": []}"
            dataManager.saveJsonToFile(context, createJsonString)
            dataManager.getJsonFromFile(context)!!
        }

        workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        if (workouts.workouts.isEmpty()){
            binding.emptyLayoutInfo.visibility = View.VISIBLE
        } else{
            binding.emptyLayoutInfo.visibility = View.GONE
        }
        binding.rvWorkouts.layoutManager = LinearLayoutManager(context)//Set RecyclerView to linearLayout
        val itemAdapter = WorkoutsAdapter(requireContext(), workouts.workouts, this)//Set ItemAdapter to Workouts workout from Gson created obj
        binding.rvWorkouts.adapter = itemAdapter //Set the RecyclerView adapter to itemAdapter

        binding.quickBtn.visibility = View.GONE
        binding.createBtn.visibility = View.GONE


        binding.mainBtn.setOnClickListener {
            checkIfBtnAreShowing()
        }

        binding.quickBtn.setOnClickListener {
            val launchQuick = Intent(this.context, QuickActivity::class.java)
            startActivity(launchQuick)
        }

        binding.createBtn.setOnClickListener{
            val launchCreate = Intent(this.context, CreateActivity::class.java)
            startActivity(launchCreate)
        }

        binding.createPresetBtn.setOnClickListener{
            val pager = activity?.findViewById<ViewPager2>(R.id.viewPagerHome)
            pager?.currentItem = 1
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onItemClick(item: WorkoutsModelClass, adapterPosition : Int) { //Check on item clicked passing item and item position
        val launchDetails = Intent(this.context, InfoActivity::class.java)
        launchDetails.putExtra("selected_workout" , item as Serializable) //Save item on Intend
        launchDetails.putExtra("position" , adapterPosition as Serializable) //Save item position on Intend
        startActivity(launchDetails)
    }

    private fun checkIfBtnAreShowing(){
        if (!showingOpts) {
            binding.quickBtn.visibility = View.VISIBLE
            binding.createBtn.visibility = View.VISIBLE
            binding.createPresetBtn.visibility = View.VISIBLE

            binding.mainBtn.rotation = 45F
            showingOpts = true
            ObjectAnimator.ofFloat(binding.mainBtn, "rotation", 45f*7).apply {
                duration = 500
                start()
            }
            val animateBtns = ObjectAnimator.ofFloat(binding.quickBtn, "translationY", -450f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(binding.createBtn, "translationY", -300f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(binding.createPresetBtn, "translationY", -150f).apply {
                duration = 300
                start()
            }
            animateBtns.doOnEnd {
                binding.quickTxtView.visibility = View.VISIBLE
                binding.createTxtView.visibility = View.VISIBLE
                binding.createPresetTxtView.visibility = View.VISIBLE
            }
        } else{
            binding.quickTxtView.visibility = View.GONE
            binding.createTxtView.visibility = View.GONE
            binding.createPresetTxtView.visibility = View.GONE
            ObjectAnimator.ofFloat(binding.mainBtn, "rotation", 0f).apply {
                duration = 500
                start()
            }
            val animateBtns = ObjectAnimator.ofFloat(binding.quickBtn, "translationY", 0f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(binding.createBtn, "translationY", 0f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(binding.createPresetBtn, "translationY", 0f).apply {
                duration = 300
                start()
            }
            animateBtns.doOnEnd {
                binding.quickBtn.visibility = View.GONE
                binding.createBtn.visibility = View.GONE
                binding.createPresetBtn.visibility = View.GONE
            }
            showingOpts = false
        }
    }


}
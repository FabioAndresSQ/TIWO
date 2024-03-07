package com.faesfa.tiwo.HomeFragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.datastore.preferences.preferencesDataStore
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
    private var tutorialStep = 0
    private var tutorialCompleted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root //save inflater to view

        checkIfBtnAreShowing()

        val sharedPref = activity?.getSharedPreferences(getString(R.string.main_tutorial_status), Context.MODE_PRIVATE)
        if (sharedPref != null) {
            tutorialCompleted = sharedPref.getBoolean("main_tutorial_completed", false)
            Log.i("Saved Preference", "onCreateView: $tutorialCompleted")
        }

        if (!tutorialCompleted){

            binding.tutorialLayout.visibility = View.VISIBLE

            binding.tutorialLayout.setOnClickListener {
                tutorialStep++
                when (tutorialStep){
                    1 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.startTutorial, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialStep.visibility = View.VISIBLE
                            binding.bottomArrow.visibility = View.VISIBLE
                            ObjectAnimator.ofFloat(binding.tutorialStep, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.bottomArrow, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                        }
                        binding.tutorialLayout.elevation = 14F
                    }
                    2 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStep, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.bottomArrow, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialStep.text = "Estas son las opciones que podras usar:\n\n" +
                                    "EJERCICIO RAPIDO: Podras iniciar un entrenamiento rapido sin necesidad de guardarlo en tus ejercicios\n\n" +
                                    "CREAR EJERCICIO: Podras crear tu ejercicio personalizado desde cero eligiendo las series y el tiempo o repeticiones que entrenaras\n\n" +
                                    "EJERCICIO PREESTABLECIDO: Podras acceder a una lista de mas de 1200 ejercicios que podras ajustar con tus propias series y tiempo"
                            ObjectAnimator.ofFloat(binding.tutorialStep, "translationY", -450f).apply {
                                duration = 0
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.bottomArrow, "translationY", -450f).apply {
                                duration = 0
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.tutorialStep, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.bottomArrow, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                        }
                        checkIfBtnAreShowing()
                    }
                    3 -> {
                        checkIfBtnAreShowing()
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStep, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.bottomArrow, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.topArrowLeft, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                        }
                    }
                    4 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.topArrowLeft, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialStepPager.text = "Aqui vas a encontrar los presets disponibles para crear tus propios ejercicios en base a ellos"
                            binding.tutorialStepPager.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                            ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.topArrowRight, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                        }
                    }
                    5 -> {

                        val prefs = activity?.getSharedPreferences(getString(R.string.main_tutorial_status), Context.MODE_PRIVATE)?.edit()
                        prefs?.putBoolean("main_tutorial_completed", true)
                        prefs?.apply()
                        tutorialCompleted = true

                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.topArrowRight, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.tutorialLayout, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialLayout.visibility = View.GONE
                            val pager = activity?.findViewById<ViewPager2>(R.id.viewPagerHome)
                            pager?.currentItem = 1

                        }
                    }
                }
            }
        } else {
            binding.tutorialLayout.visibility = View.GONE
        }



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
            if (tutorialCompleted) {
                val launchQuick = Intent(this.context, QuickActivity::class.java)
                startActivity(launchQuick)
            }
        }

        binding.createBtn.setOnClickListener{
            if (tutorialCompleted){
                val launchCreate = Intent(this.context, CreateActivity::class.java)
                startActivity(launchCreate)
            }
        }

        binding.createPresetBtn.setOnClickListener{
            if (tutorialCompleted) {
                val pager = activity?.findViewById<ViewPager2>(R.id.viewPagerHome)
                pager?.currentItem = 1
            }
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
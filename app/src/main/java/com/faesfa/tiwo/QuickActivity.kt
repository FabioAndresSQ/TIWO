package com.faesfa.tiwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.faesfa.tiwo.databinding.ActivityQuickBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import java.io.Serializable

class QuickActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuickBinding

    private lateinit var workout : WorkoutsModelClass
    private var sets = 3 //Default sets 3
    private var reps = 10
    private var interval = 1.5
    private var workMinutes = 0
    private var workSeconds = 0
    private var restMinutes = 0
    private var restSeconds = 0
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.includeQuickBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        //INITIALIZING BANNER ADS AND REQUESTING IT
        startBannerAds()

        //Listener for Switch
        changeVisibility(binding.quickWorking.isChecked)
        binding.quickWorking.setOnCheckedChangeListener { _, isChecked ->
            changeVisibility(isChecked)
        }

        //Set listener to sets add or remove
        binding.quickSetsTxt.text = sets.toString()
        binding.quickAddSetsBtn.setOnClickListener {
            if (sets > 0) {
                sets++
                binding.quickSetsTxt.text = sets.toString()
                binding.quickRemoveSetsBtn.visibility = View.VISIBLE
            }
        }
        binding.quickRemoveSetsBtn.setOnClickListener {
            if (sets > 1){
                sets--
                binding.quickSetsTxt.text = sets.toString()
            } else if (sets == 1) {
                binding.quickRemoveSetsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Reps
        binding.quickAddRepsBtn.setOnClickListener {
            if (reps > 0) {
                reps++
                binding.quickRepsTxt.text = reps.toString()
                binding.quickRemoveRepsBtn.visibility = View.VISIBLE
            }
        }
        binding.quickRemoveRepsBtn.setOnClickListener {
            if (reps > 1){
                reps--
                binding.quickRepsTxt.text = reps.toString()
            } else if (reps == 1) {
                binding.quickRemoveRepsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Interval
        binding.quickAddIntervalBtn.setOnClickListener {
            if (interval > 0.0) {
                interval += 0.5
                binding.quickIntervalTxt.text = interval.toString()
                binding.quickRemoveIntervalBtn.visibility = View.VISIBLE
            }
        }
        binding.quickRemoveIntervalBtn.setOnClickListener {
            if (interval > 0.6){
                interval -= 0.5
                binding.quickIntervalTxt.text = interval.toString()
            } else if (interval == 0.5) {
                binding.quickRemoveIntervalBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for the NumberPickers on Work Time
        binding.workMinutesQuick.maxValue = 59
        binding.workMinutesQuick.minValue = 0
        binding.workSecondsQuick.maxValue = 59
        binding.workSecondsQuick.minValue = 0
        binding.workMinutesQuick.setOnValueChangedListener { _, _, newVal ->
            workMinutes = newVal
        }
        binding.workSecondsQuick.setOnValueChangedListener { _, _, newVal ->
            workSeconds = newVal
        }

        //Listener for the NumberPickers on Rest Time
        binding.restMinutesQuick.maxValue = 59
        binding.restMinutesQuick.minValue = 0
        binding.restSecondsQuick.maxValue = 59
        binding.restSecondsQuick.minValue = 0
        binding.restMinutesQuick.setOnValueChangedListener { _, _, newVal ->
            restMinutes = newVal
        }
        binding.restSecondsQuick.setOnValueChangedListener { _, _, newVal ->
            restSeconds = newVal
        }

        //listener for Start Quick timer Btn
        binding.quickStartBtn.setOnClickListener {
            val workTime = (workMinutes * 60) + workSeconds
            val restTime = (restMinutes * 60) + restSeconds
            if (binding.quickWorking.isChecked){ //Working with reps
                if (reps < 1){ //Checks for Number of reps to not be 0
                    Toast.makeText(this, getString(R.string.repsCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (interval < 1){ //Checks for reps interval to not be 0
                    Toast.makeText(this, getString(R.string.intervalCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (restTime < 1){ //Checks that Rest is not 0
                    Toast.makeText(this, getString(R.string.restTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (sets < 1) {
                    Toast.makeText(this, getString(R.string.setsCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                //Assign values to workout Obj
                workout  = WorkoutsModelClass("","Quick Timer", sets, true,reps,interval,0,restTime, "")
                //Launch Timer Activity with workout
                val launchTimer = Intent(this, TimerActivity::class.java)
                launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
                startActivity(launchTimer)

            } else { //Working with Time

                if (workTime < 1){ //Checks for work to not be 0
                    Toast.makeText(this, getString(R.string.workTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (restTime < 1){ //Checks for rest to not be 0
                    Toast.makeText(this, getString(R.string.restTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (sets < 1) {
                    Toast.makeText(this, getString(R.string.setsCheckErrorToast), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                //Assign values to workout Obj
                workout  = WorkoutsModelClass("", "Quick Timer", sets, false,0,0.0,workTime,restTime, "")
                //Launch Timer Activity with workout
                val launchTimer = Intent(this, TimerActivity::class.java)
                launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
                startActivity(launchTimer)
            }
        }
    }

    private fun changeVisibility(workReps : Boolean){ //Set visibility depending on working with reps or time
        if (!workReps){ //Working time
            binding.quickNumRepsTitle.visibility = View.GONE
            binding.quickRepsTxt.visibility = View.GONE
            binding.quickAddRepsBtn.visibility = View.GONE
            binding.quickRemoveRepsBtn.visibility = View.GONE

            binding.quickRepsIntervalTitle.visibility = View.GONE
            binding.quickIntervalTxt.visibility = View.GONE
            binding.quickAddIntervalBtn.visibility = View.GONE
            binding.quickRemoveIntervalBtn.visibility = View.GONE
            binding.quickSecondsIntervalTxt.visibility = View.GONE

            binding.quickWorkTimeTitle.visibility = View.VISIBLE
            binding.workMinutesQuick.visibility = View.VISIBLE
            binding.workSecondsQuick.visibility = View.VISIBLE
            binding.dotsWorkTimeQuick.visibility = View.VISIBLE
        } else { //Working reps
            binding.quickNumRepsTitle.visibility = View.VISIBLE
            binding.quickRepsTxt.visibility = View.VISIBLE
            binding.quickAddRepsBtn.visibility = View.VISIBLE
            binding.quickRemoveRepsBtn.visibility = View.VISIBLE

            binding.quickRepsIntervalTitle.visibility = View.VISIBLE
            binding.quickIntervalTxt.visibility = View.VISIBLE
            binding.quickAddIntervalBtn.visibility = View.VISIBLE
            binding.quickRemoveIntervalBtn.visibility = View.VISIBLE
            binding.quickSecondsIntervalTxt.visibility = View.VISIBLE

            binding.quickWorkTimeTitle.visibility = View.GONE
            binding.workMinutesQuick.visibility = View.GONE
            binding.workSecondsQuick.visibility = View.GONE
            binding.dotsWorkTimeQuick.visibility = View.GONE
        }
    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewQuick.loadAd(adRequest)

        binding.adViewQuick.adListener = object : AdListener(){
            override fun onAdClicked() {
                Log.d("AD_BANNER", "AD LOADED CLICKED")
                super.onAdClicked()
            }

            override fun onAdClosed() {
                Log.d("AD_BANNER", "AD LOADED CLOSED")
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e("AD_BANNER", p0.toString())
                super.onAdFailedToLoad(p0)
            }

            override fun onAdImpression() {
                Log.d("AD_BANNER", "AD IMPRESSION COUNTED")
                super.onAdImpression()
            }

            override fun onAdLoaded() {
                Log.d("AD_BANNER", "AD LOADED SUCCESSFUL")
                super.onAdLoaded()
            }

            override fun onAdOpened() {
                Log.d("AD_BANNER", "AD LOADED OPENED OVERLAY")
                super.onAdOpened()
            }
        }
    }

}
package com.faesfa.tiwo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.faesfa.tiwo.databinding.ActivityInfoBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

class InfoActivity : AppCompatActivity() {
    //Initialize everything
    private lateinit var binding: ActivityInfoBinding
    private lateinit var workouts: Workouts
    private lateinit var dataManager: DataManager
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        dataManager = DataManager()
        val workout : WorkoutsModelClass = intent?.getSerializableExtra("selected_workout") as WorkoutsModelClass //Save workout got from Prev Activity
        val position = intent?.getSerializableExtra("position") as Int //Save position got from Prev Activity

        //INITIALIZING BANNER ADS AND REQUESTING IT
        startBannerAds()

        when (workout.category){
            "Chest" -> {binding.infoImg.setImageResource(R.drawable.chest_ic)}
            "Back" -> {binding.infoImg.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {binding.infoImg.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {binding.infoImg.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {binding.infoImg.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {binding.infoImg.setImageResource(R.drawable.abs_ic)}
        }
        @RequiresApi(Build.VERSION_CODES.M)
        binding.infoImg.borderColor = this.getColor(R.color.AppColor)
        binding.infoImg.borderWidth = 5

        binding.infoNameTxt.text = workout.name.uppercase()
        binding.infoSetsTxt.text = workout.sets.toString()
        if (workout.reps){ //Working with reps, set visibility
            binding.infoReps.visibility = View.VISIBLE
            binding.infoRepsTxt.visibility = View.VISIBLE
            binding.infoRepsTxt.text = workout.num_reps.toString()
            binding.infoWorkTitle.text = this.getString(R.string.infoNumRepsIntervalTitle) //Set title to working with Reps
            val duration = "${workout.reps_time} ${getString(R.string.infoIntervalSecs)}"
            binding.infoWorkTxt.text = duration
        }else{ //Working with Time, set visibility
            binding.infoReps.visibility = View.GONE
            binding.infoRepsTxt.visibility = View.GONE
            binding.infoWorkTitle.text = this.getString(R.string.infoWorkTimeTitle) //Set title to working with time
            val duration = dataManager.convertTime(workout.work_time)
            binding.infoWorkTxt.text = duration
        }
        val restDur = dataManager.convertTime(workout.rest_time)
        binding.infoRestTxt.text = restDur

        //Set listeners for Start and delete Btn
        binding.infoStartBtn.setOnClickListener {
            val launchTimer = Intent(this, TimerActivity::class.java)
            launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
            startActivity(launchTimer)
        }

        binding.infoDeleteBtn.setOnClickListener {
            try {
                val jsonString = dataManager.getJsonFromFile(this)!! //Get String from File
                workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn Json String into Workouts obj
                workouts.workouts.removeAt(position) //Remove workout object on specified position from workouts
                val jsonCute = GsonBuilder().setPrettyPrinting().create()
                val newJson = jsonCute.toJson(workouts) //Create the new Json string with Gson
                dataManager.saveJsonToFile(this, newJson) //Save Json to file
                Toast.makeText(this, "Workout Deleted", Toast.LENGTH_SHORT).show()

                val launchHome = Intent(this, MainActivity::class.java)
                startActivity(launchHome)
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(applicationContext, "Error to Delete Workout", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewInfo.loadAd(adRequest)

        binding.adViewInfo.adListener = object : AdListener(){
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




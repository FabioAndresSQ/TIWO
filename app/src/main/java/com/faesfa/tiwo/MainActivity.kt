package com.faesfa.tiwo

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faesfa.tiwo.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import java.io.*

class MainActivity : AppCompatActivity(), WorkoutsAdapter.OnItemClickListener {
    //Initialize everything
    private  lateinit var binding: ActivityMainBinding
    private var backPressedOnce = false
    private lateinit var workouts : Workouts
    private lateinit var dataManager: DataManager
    private lateinit var jsonString : String
    private var showingOpts = true
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashScreen.setKeepOnScreenCondition{ false }

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        //INITIALIZING BANNER ADS AND REQUESTING IT
        startBannerAds()

        dataManager = DataManager()
        checkIfBtnAreShowing()

        binding.quickBtn.visibility = View.GONE
        binding.createBtn.visibility = View.GONE


        binding.mainBtn.setOnClickListener {
            checkIfBtnAreShowing()
        }

        binding.quickBtn.setOnClickListener {
            val launchQuick = Intent(this, QuickActivity::class.java)
            startActivity(launchQuick)
        }

        binding.createBtn.setOnClickListener{
            val launchCreate = Intent(this, CreateActivity::class.java)
            startActivity(launchCreate)
        }
        //Get data from Json file
        jsonString = try {
            dataManager.getJsonFromFile(this)!!
        } catch (e : Exception){ //if File is Empty or null Create it
            val createJsonString = "{\"workouts\": []}"
            dataManager.saveJsonToFile(this, createJsonString)
            dataManager.getJsonFromFile(this)!!
        }

        workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        Log.d("START", workouts.workouts.toString())
        if (workouts.workouts.isEmpty()){
            binding.emptyLayoutInfo.visibility = View.VISIBLE
        } else{
            binding.emptyLayoutInfo.visibility = View.GONE
        }
        val mRecyclerView = findViewById<RecyclerView>(R.id.rvWorkouts)
        mRecyclerView.layoutManager = LinearLayoutManager(this)//Set RecyclerView to linearLayout
        val itemAdapter = WorkoutsAdapter(this, workouts.workouts, this)//Set ItemAdapter to Workouts workout from Gson created obj
        mRecyclerView.adapter = itemAdapter //Set the RecyclerView adapter to itemAdapter

    }

    override fun onItemClick(item: WorkoutsModelClass, adapterPosition : Int) { //Check on item clicked passing item and item position
        val launchDetails = Intent(this, InfoActivity::class.java)
        launchDetails.putExtra("selected_workout" , item as Serializable) //Save item on Intend
        launchDetails.putExtra("position" , adapterPosition as Serializable) //Save item position on Intend
        startActivity(launchDetails)
    }

    override fun onBackPressed() { //Control the back button pressed
        if (backPressedOnce) {
            finishAffinity() //Close App
        }
        this.backPressedOnce = true
        Toast.makeText(this, getString(R.string.backToExitApp), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce=false },2000)
    }

    private fun checkIfBtnAreShowing(){
        if (!showingOpts) {
            binding.quickBtn.visibility = View.VISIBLE
            binding.createBtn.visibility = View.VISIBLE

            binding.mainBtn.rotation = 45F
            showingOpts = true
            ObjectAnimator.ofFloat(binding.mainBtn, "rotation", 45f*8).apply {
                duration = 500
                start()
            }
            val animateBtns = ObjectAnimator.ofFloat(binding.quickBtn, "translationY", -320f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(binding.createBtn, "translationY", -160f).apply {
                duration = 300
                start()
            }
            animateBtns.doOnEnd {
                binding.quickTxtView.visibility = View.VISIBLE
                binding.createTxtView.visibility = View.VISIBLE
            }
        } else{
            binding.quickTxtView.visibility = View.GONE
            binding.createTxtView.visibility = View.GONE
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
            animateBtns.doOnEnd {
                binding.quickBtn.visibility = View.GONE
                binding.createBtn.visibility = View.GONE
            }
            showingOpts = false
        }
    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewHome.loadAd(adRequest)

        binding.adViewHome.adListener = object : AdListener(){
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
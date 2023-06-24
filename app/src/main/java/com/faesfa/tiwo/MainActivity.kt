package com.faesfa.tiwo

import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.faesfa.tiwo.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions.SwipeGestureDirection
import com.google.android.material.tabs.TabLayoutMediator
import java.io.*

class MainActivity : AppCompatActivity() {
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
        splashScreen.setKeepOnScreenCondition{ false }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        //INITIALIZING BANNER ADS AND REQUESTING IT
        startBannerAds()

        /*binding.presetsBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.containerView, PresetsFragment()).commit()
            binding.presetsBtn.setTextColor(resources.getColor(R.color.AppColor))
            binding.workoutsBtn.setTextColor(resources.getColor(R.color.black))
            Toast.makeText(this,"Ubicacion: ${binding.presetsBtn.x / binding.workoutsBtn.x}", Toast.LENGTH_SHORT).show()
            ObjectAnimator.ofFloat(binding.bottomLine, "translationX", binding.presetsBtn.x - binding.workoutsBtn.x).apply {
                duration = 500
                start()
            }
        }

        binding.workoutsBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.containerView, MainFragment()).commit()
            binding.workoutsBtn.setTextColor(resources.getColor(R.color.AppColor))
            binding.presetsBtn.setTextColor(resources.getColor(R.color.black))
            val ub = IntArray(2)
            val location = binding.workoutsBtn.getLocationInWindow(ub)
            Toast.makeText(this,"Ubicacion: ${binding.workoutsBtn.x}", Toast.LENGTH_SHORT).show()
            ObjectAnimator.ofFloat(binding.bottomLine, "translationX", 5f).apply {
                duration = 500
                start()
            }
        }*/



        binding.viewPagerHome.adapter = HomeAdapter(this)
        TabLayoutMediator(binding.tabLayoutMenu,binding.viewPagerHome){tab, index ->
            tab.text = when (index){
                0 -> {resources.getString(R.string.mainHomeTitle)}
                1 -> {resources.getString(R.string.mainPresetsTitle)}
                else -> { throw NotFoundException("Tab Position not Found")}
            }
        }.attach()

    }

    override fun onBackPressed() { //Control the back button pressed
        if (backPressedOnce) {
            finishAffinity() //Close App
        }
        this.backPressedOnce = true
        Toast.makeText(this, getString(R.string.backToExitApp), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce=false },2000)
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
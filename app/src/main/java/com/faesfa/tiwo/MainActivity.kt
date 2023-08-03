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
import androidx.viewpager2.widget.ViewPager2
import com.faesfa.tiwo.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions.SwipeGestureDirection
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    //Initialize everything
    private  lateinit var binding: ActivityMainBinding
    private var backPressedOnce = false
    private lateinit var toolBar : Toolbar
    private lateinit var pager : ViewPager2

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
        pager = binding.viewPagerHome
        if (pager.currentItem != 0) {
            pager.currentItem = 0
        } else {
            if (backPressedOnce) {
                finishAffinity() //Close App
            }
            this.backPressedOnce = true
            Toast.makeText(this, getString(R.string.backToExitApp), Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce=false },2000)
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
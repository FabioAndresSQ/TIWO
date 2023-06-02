package com.faesfa.tiwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.faesfa.tiwo.databinding.ActivityCreateBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private lateinit var toolBar : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.includeCreateBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        startBannerAds()

    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewCreate.loadAd(adRequest)

        binding.adViewCreate.adListener = object : AdListener(){
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
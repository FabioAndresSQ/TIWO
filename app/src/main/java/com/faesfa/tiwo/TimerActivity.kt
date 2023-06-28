package com.faesfa.tiwo

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.view.marginStart
import com.faesfa.tiwo.databinding.ActivityTimerBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class TimerActivity : AppCompatActivity() {
    //Initialize everything
    @Inject lateinit var dataManager: DataManager

    private lateinit var binding: ActivityTimerBinding
    private lateinit var workout : WorkoutsModelClass
    private var numSets = 0
    private var currentSet = 1
    private var reps = 0
    private var work = 0
    private var rest = 0
    private var isPaused = false
    private var started = false
    private var resting = false
    private var backPressedOnce = false
    private var touchedOnce = 0
    private var touchedSkipOnce = 0
    private var touchedReturnOnce = 0
    private var showedFirst = false

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startDownTimer: CountDownTimer
    private lateinit var timerInterval: CountDownTimer
    private var initialWorkTime: Long = 0
    private var initialRestTime: Long = 0
    private var countDownInterval: Long = 1000
    private var countDownInPause: Long = 0
    private var restFormat: String = ""
    private var workFormat: String = ""
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var vibratorDur = 100L
    private var vibrationEnabled = true
    private var soundEnabled = true
    private lateinit var toolBar : Toolbar
    private var finalInterstitialAd : InterstitialAd? = null
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(getString(R.string.sound_vibration_setting), Context.MODE_PRIVATE)
        soundEnabled = sharedPref.getBoolean("sound", true)
        vibrationEnabled = sharedPref.getBoolean("vibration", true)

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)
        supportActionBar?.elevation  = 0f

        activity = this
        workout = intent?.getSerializableExtra("selected_workout") as WorkoutsModelClass
        reps = workout.num_reps
        work = workout.work_time
        rest = workout.rest_time
        numSets = workout.sets

        startBannerAds()
        val adRequestInterstitial = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-2716842126108084/3865049547", adRequestInterstitial, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AD INTERSTITIAL", adError.toString())
                finalInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("AD INTERSTITIAL", "Ad was loaded.")
                finalInterstitialAd = interstitialAd
            }
        })

        // Hiding and showing views according to workout mode and setting values
        if (workout.reps) { //Working with Reps
            initialWorkTime = ((workout.num_reps * workout.reps_time) * 1000 + 1000).toLong() //Set Timer Value
            workFormat = reps.toString() + " " + getString(R.string.workingRepsTimer)
            binding.timerMinTxt.visibility = View.GONE
            binding.minutesTimerLbl.visibility = View.GONE
            binding.secondsTimerLbl.visibility = View.GONE
            binding.timerSecTxt.textSize = 200F
        } else { //Working with Time
            initialWorkTime = (workout.work_time * 1000 + 1000).toLong() //Set Timer Value
            val workTimeFormat = dataManager.convertTimeTimer(work)
            workFormat = getString(R.string.workTimer) + " " + workTimeFormat[0] + ":" + workTimeFormat[1]
            binding.timerMinTxt.text = workTimeFormat[0]
            binding.timerSecTxt.text = workTimeFormat[1]
        }
        val restTimeFormat = dataManager.convertTimeTimer(rest)
        binding.pauseAnimationLayout.visibility = View.VISIBLE
        binding.pauseAnimationLayout.alpha = 0f
        if (!soundEnabled){
            binding.soundBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_volume_off_24))
        }
        if (!vibrationEnabled){
            binding.vibrationBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_mobile_off_24))
        }

        //Assigning values and assets
        restFormat = getString(R.string.restTimer) + " " + restTimeFormat[0] + ":" + restTimeFormat[1]
        binding.timerNextTxt.text = restFormat
        binding.timerSetsTxt.text = currentSet.toString()
        initialRestTime = (workout.rest_time * 1000 + 1000).toLong()
        binding.timerNameTxt.text = workout.name.uppercase()
        when (workout.category){
            "Chest" -> {binding.timerImage.setImageResource(R.drawable.chest_ic)}
            "Back" -> {binding.timerImage.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {binding.timerImage.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {binding.timerImage.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {binding.timerImage.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {binding.timerImage.setImageResource(R.drawable.abs_ic)}
        }
        binding.startingLayout.visibility = View.GONE
        binding.goPreviousStateBtn.visibility = View.GONE
        started = false

        startingTimer(initialWorkTime) //Start Short Timer Before Starting

        binding.skipTimerBtn.setOnClickListener {
            if (started && !isPaused) {
                touchedSkipOnce++
                Handler(Looper.getMainLooper()).postDelayed({ //Double tap handler
                    if (touchedSkipOnce == 1) {
                        Toast.makeText(this, getString(R.string.doubleTapSkip), Toast.LENGTH_SHORT)
                            .show()
                    } else if (touchedSkipOnce == 2) {
                        val animateNumbersLayout =
                            ObjectAnimator.ofFloat(binding.timerNumbersLayout, "alpha", 0f).apply {
                                duration = 1000
                                start()
                            }
                        animateNumbersLayout.doOnEnd {
                            skipCurrentState()
                            ObjectAnimator.ofFloat(binding.timerNumbersLayout, "alpha", 1f).apply {
                                duration = 1000
                                start()
                            }
                        }
                        val animateText =
                            ObjectAnimator.ofFloat(binding.timerCurrentState, "alpha", 0f).apply {
                                duration = 1000
                                start()
                            }
                        animateText.doOnEnd {
                            ObjectAnimator.ofFloat(binding.timerCurrentState, "alpha", 1f).apply {
                                duration = 1000
                                start()
                            }
                        }
                    }
                    touchedSkipOnce = 0
                }, 200)
            }
        }

        binding.goPreviousStateBtn.setOnClickListener {
                if (started && !isPaused) {
                    touchedReturnOnce++
                    Handler(Looper.getMainLooper()).postDelayed({ //Double tap handler
                        if (touchedReturnOnce == 1) {
                            Toast.makeText(this, getString(R.string.doubleTapReturn), Toast.LENGTH_SHORT).show()
                        } else if (touchedReturnOnce == 2) {
                            val animateNumbersLayout =
                                ObjectAnimator.ofFloat(binding.timerNumbersLayout, "alpha", 0f).apply {
                                    duration = 1000
                                    start()
                                }
                            animateNumbersLayout.doOnEnd {
                                returnPreviousState()
                                ObjectAnimator.ofFloat(binding.timerNumbersLayout, "alpha", 1f).apply {
                                    duration = 1000
                                    start()
                                }
                            }
                            val animateText =
                                ObjectAnimator.ofFloat(binding.timerCurrentState, "alpha", 0f).apply {
                                    duration = 1000
                                    start()
                                }
                            animateText.doOnEnd {
                                ObjectAnimator.ofFloat(binding.timerCurrentState, "alpha", 1f).apply {
                                    duration = 1000
                                    start()
                                }
                            }
                        }
                        touchedReturnOnce = 0
                    }, 200)
                }
            }

        //Set listener to The Layout for Pause/UnPause Functionality
        binding.timerLayout.setOnClickListener {
            val animatePauseLayout =
                ObjectAnimator.ofFloat(binding.pauseAnimationLayout, "alpha", 1f).apply {
                    duration = 100
                    start()
                }
            animatePauseLayout.doOnEnd {
                ObjectAnimator.ofFloat(binding.pauseAnimationLayout, "alpha", 0f).apply {
                    duration = 600
                    start()
                }
            }
            touchedOnce++
            Handler(Looper.getMainLooper()).postDelayed({ //Double tap handler
                if (touchedOnce == 1) {
                    Toast.makeText(this, getString(R.string.doubleTapPause), Toast.LENGTH_SHORT).show()
                } else if (touchedOnce == 2){
                    if (!isPaused) {
                        pauseTimer()
                    } else {
                        resumeTimer()
                    }
                }
                touchedOnce = 0
            },200)
        }

        binding.soundBtn.setOnClickListener {
            if(started && !isPaused) {
                Log.d("SettingPressed", "Sound Clicked")
                soundEnabled = !soundEnabled
                val prefs = getSharedPreferences(getString(R.string.sound_vibration_setting), Context.MODE_PRIVATE).edit()
                prefs.putBoolean("sound", soundEnabled)
                prefs.apply()
                if (soundEnabled) {
                    binding.soundBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_volume_up_24))
                } else {
                    binding.soundBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_volume_off_24
                        )
                    )
                }
            }
        }

        binding.vibrationBtn.setOnClickListener {
            if (started && !isPaused) {
                Log.d("SettingPressed", "Vibration Clicked")
                vibrationEnabled = !vibrationEnabled
                val prefs = getSharedPreferences(getString(R.string.sound_vibration_setting), Context.MODE_PRIVATE).edit()
                prefs.putBoolean("vibration", vibrationEnabled)
                prefs.apply()
                if (vibrationEnabled) {
                    binding.vibrationBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_vibration_24))
                } else {
                    binding.vibrationBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_mobile_off_24))
                }
            }
        }
    }

    private fun skipCurrentState(){
        countDownTimer.cancel()
        timerInterval.cancel()
        if (resting) {
            //Pass to next Set and workout
            resting = false
            numSets--
            currentSet++
            if (numSets > 0){
                startingTimer(initialWorkTime)
            } else {//Sets is 0
                launchHome()
            }
        } else {
            //Pass to resting time
            startRestTimer(initialRestTime)
        }
    }

    private fun returnPreviousState(){
        countDownTimer.cancel()
        timerInterval.cancel()
        if (resting) {
            //Pass to next Set and workout
            resting = false
            startingTimer(initialWorkTime)
        } else {
            //Pass to resting time
            numSets++
            currentSet--
            startRestTimer(initialRestTime)
        }
    }

    private fun startingTimer(time: Long) { //Short timer before Starting Workout
        started = false
        binding.skipTimerBtn.visibility = View.GONE
        binding.soundBtn.visibility = View.GONE
        binding.vibrationBtn.visibility = View.GONE
        if (currentSet == 1 && resting){
            binding.goPreviousStateBtn.visibility = View.GONE
        } else if (currentSet > 1){
            binding.goPreviousStateBtn.visibility = View.GONE
        }else {
            binding.goPreviousStateBtn.visibility = View.GONE
        }
        binding.startingLayout.visibility = View.VISIBLE
        binding.timerLayout.visibility = View.GONE
        countDownInPause = initialWorkTime
        countDownInterval = 1000L
        binding.startingLayout.setBackgroundColor(resources.getColor(R.color.timerGrayLightStarting))
        startDownTimer = object : CountDownTimer(6000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                countDownInPause = initialWorkTime
                actionOnIntervalStarting((millisUntilFinished))
                actionOnIntervalStarting((countDownInterval))
                if ((millisUntilFinished/countDownInterval) > 3){
                    binding.startingLbl.text = getString(R.string.getReady)
                    binding.secsToStart.visibility = View.GONE
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                } else if ((millisUntilFinished/countDownInterval) <= 3 && (millisUntilFinished/countDownInterval) > 0.99){
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                    binding.startingLbl.text = getString(R.string.starting)
                    binding.secsToStart.text = (millisUntilFinished/countDownInterval).toString()
                    binding.secsToStart.visibility = View.VISIBLE
                } else if ((millisUntilFinished/countDownInterval) < 1){
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                    binding.secsToStart.text = getString(R.string.timerStartingFinalValue)
                }
            }
            override fun onFinish() {
                started = true
                startWorkTimer(time) //Start Workout timer after this is finished
                binding.startingLayout.visibility = View.GONE
                binding.timerLayout.visibility = View.VISIBLE
            }
        }
        startDownTimer.start() //Start Short Timer
    }

    private fun actionOnIntervalStarting(num: Long){ //actions to do during workout

    }

    private fun startWorkTimer(time : Long){ //Workout Timer
        binding.skipTimerBtn.visibility = View.VISIBLE
        binding.soundBtn.visibility = View.VISIBLE
        binding.vibrationBtn.visibility = View.VISIBLE
        if (currentSet > 1){
            binding.goPreviousStateBtn.visibility = View.VISIBLE
        }else {
            binding.goPreviousStateBtn.visibility = View.GONE
        }
        binding.timerNextTxt.text = restFormat
        binding.timerSetsTxt.text = (currentSet).toString()
        binding.timerCurrentState.text = getString(R.string.timerWorkTxt)
        if (workout.reps){ //Working with Reps, Set Visibility
            countDownInterval = (workout.reps_time * 1000).toLong()
            binding.timerMinTxt.visibility = View.GONE
            binding.minutesTimerLbl.visibility = View.GONE
            binding.secondsTimerLbl.visibility = View.GONE
            binding.repsTimerLbl.visibility = View.VISIBLE
            binding.timerSecTxt.textSize = 200F
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    binding.timerSecTxt.text = (millisUntilFinished/countDownInterval).toString()
                }
                override fun onFinish() {
                    startRestTimer(initialRestTime) //Start rest time after this is finished
                }
            }
            countDownTimer.start() //Start Reps Timer
        } else{ //Working with Time, Set Visibility
            countDownInterval = 1000
            binding.repsTimerLbl.visibility = View.GONE
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    work = (millisUntilFinished/1000).toInt()
                    val timeFormat = dataManager.convertTimeTimer(work)
                    binding.timerMinTxt.text = timeFormat[0]
                    binding.timerSecTxt.text = timeFormat[1]
                }
                override fun onFinish() {
                    startRestTimer(initialRestTime) //Start rest time after this is finished
                }

            }
            countDownTimer.start() //Start Time Timer
        }

        //Timer Background to do things in between
        timerInterval = object  : CountDownTimer(time, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                actionOnInterval(false)
                if ((millisUntilFinished/countDownInterval) < 4){
                    actionOnInterval(true)
                }
            }
            override fun onFinish() {
                showedFirst = false
            }
        }
        timerInterval.start() //Start Timer Background

    }

    private fun startRestTimer(time : Long){ //Rest Timer
        resting = true
        binding.skipTimerBtn.visibility = View.VISIBLE
        binding.soundBtn.visibility = View.VISIBLE
        binding.vibrationBtn.visibility = View.VISIBLE
        if (currentSet == 1 && resting){
            binding.goPreviousStateBtn.visibility = View.VISIBLE
        } else if (currentSet > 1){
            binding.goPreviousStateBtn.visibility = View.VISIBLE
        }else {
            binding.goPreviousStateBtn.visibility = View.GONE
        }
        binding.timerSetsTxt.text = (currentSet).toString()
        binding.timerCurrentState.text = getString(R.string.timerRestTxt)
        binding.timerSecTxt.textSize = 150F
        if (numSets > 1) {
            binding.timerNextTxt.text = workFormat
        } else{
            binding.timerNextTxt.text = getString(R.string.end)
        }
        binding.timerMinTxt.visibility = View.VISIBLE
        binding.minutesTimerLbl.visibility = View.VISIBLE
        binding.secondsTimerLbl.visibility = View.VISIBLE
        binding.repsTimerLbl.visibility = View.GONE
        binding.timerSecTxt.marginStart.plus(60)
        countDownInterval = 1000
        countDownTimer = object : CountDownTimer(time, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                countDownInPause = millisUntilFinished
                rest = (millisUntilFinished/1000).toInt()
                val timeFormat = dataManager.convertTimeTimer(rest)
                binding.timerMinTxt.text = timeFormat[0]
                binding.timerSecTxt.text = timeFormat[1]
            }
            override fun onFinish() {
                resting = false
                numSets--
                currentSet++
                if (numSets > 0){ //Sets is greater than 0
                    startingTimer(initialWorkTime) //Start Workout Timer
                } else { //Sets is 0
                    startInterstitialAd()
                }
            }
        }
        countDownTimer.start() //Start rest Timer
    }


    private fun actionOnInterval(isFinishing : Boolean){ //actions to do during workout
    //Create sound player for interval Ticks
        val resID = resources.getIdentifier("tick", "raw", packageName)

        if (!isFinishing){
            if (vibrationEnabled){
                val phoneVibrator = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                vibratorDur = 100L
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    phoneVibrator.vibrate(VibrationEffect.createOneShot(vibratorDur,
                        VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else {
                    phoneVibrator.vibrate(vibratorDur)
                }
            }
            if (soundEnabled){
                mediaPlayer.reset()
                mediaPlayer = MediaPlayer.create(this, resID)
                mediaPlayer.start()
            }
        } else {
            ObjectAnimator.ofFloat(binding.timerSecTxt,"scaleX",1.1f, 1f).apply {
                duration = 400
                start()
            }
            ObjectAnimator.ofFloat(binding.timerSecTxt,"scaleY",1.1f, 1f).apply {
                duration = 400
                start()
            }
            ObjectAnimator.ofFloat(binding.timerMinTxt,"scaleX",1.1f, 1f).apply {
                duration = 400
                start()
            }
            ObjectAnimator.ofFloat(binding.timerMinTxt,"scaleY",1.1f, 1f).apply {
                duration = 400
                start()
            }
            if (vibrationEnabled){

                val phoneVibrator = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                vibratorDur = (vibratorDur * 1.333).toLong()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    phoneVibrator.vibrate(VibrationEffect.createOneShot(vibratorDur,
                        VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else {
                    phoneVibrator.vibrate(vibratorDur)
                }
            }
            if (soundEnabled){
                mediaPlayer.reset()
                mediaPlayer = MediaPlayer.create(this, resID)
                mediaPlayer.start()
            }
        }





    }

    override fun onPause() {
        pauseTimer()
        super.onPause()
    }

    override fun onBackPressed() { //Handle back pressed
        if (started) {
            if (backPressedOnce) {//Double pressed
                launchHome()
                countDownTimer.cancel()
                timerInterval.cancel()
            }
            this.backPressedOnce = true
            Toast.makeText(this, getString(R.string.backToExitTimer), Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce = false }, 2000)
        }
    }

    private fun launchHome(){//Launch home screen
        val launchHome = Intent(this, MainActivity::class.java)
        startActivity(launchHome)
    }

    private fun pauseTimer(){
        binding.pausedLayout.visibility =View.VISIBLE
        binding.skipTimerBtn.visibility = View.GONE
        binding.soundBtn.visibility = View.GONE
        binding.vibrationBtn.visibility = View.GONE
        if (binding.goPreviousStateBtn.visibility == View.VISIBLE){
            binding.goPreviousStateBtn.visibility = View.GONE
        }
        countDownTimer.cancel()
        if (started){timerInterval.cancel()}
        isPaused = true
    }

    private fun resumeTimer(){
        binding.pausedLayout.visibility =View.GONE
        if (resting) {
            startRestTimer(countDownInPause)
        } else {
            startingTimer(countDownInPause)
        }
        isPaused = false
    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewTimer.loadAd(adRequest)

        binding.adViewTimer.adListener = object : AdListener(){
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

    private fun startInterstitialAd(){
        if (finalInterstitialAd != null) {
            finalInterstitialAd?.show(activity)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            launchHome()//Launch home Screen
        }
        finalInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("AD INTERSTITIAL", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d("AD INTERSTITIAL", "Ad dismissed fullscreen content.")
                finalInterstitialAd = null
                launchHome()//Launch home Screen
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("AD INTERSTITIAL", "Ad failed to show fullscreen content.")
                finalInterstitialAd = null
                launchHome()//Launch home Screen
                super.onAdFailedToShowFullScreenContent(p0)
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("AD INTERSTITIAL", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("AD INTERSTITIAL", "Ad showed fullscreen content.")
            }
        }
    }

}

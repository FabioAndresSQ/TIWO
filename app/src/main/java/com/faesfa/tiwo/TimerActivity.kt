package com.faesfa.tiwo

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.marginStart

class TimerActivity : AppCompatActivity() {
    //Initialize everything
    private lateinit var timerName : TextView
    private lateinit var timerSets : TextView
    private lateinit var timerMinutes : TextView
    private lateinit var timerSeconds : TextView
    private lateinit var timerNext : TextView
    private lateinit var timerLayout : ConstraintLayout
    private lateinit var layoutContainer : ConstraintLayout
    private lateinit var startingLayout : ConstraintLayout
    private lateinit var pausedLayout : ConstraintLayout
    private lateinit var startingLbl : TextView
    private lateinit var secsToStart : TextView
    private lateinit var minutesTimerLbl : TextView
    private lateinit var secondsTimerLbl : TextView
    private lateinit var repsTimerLbl : TextView
    private lateinit var timerImage : ImageView
    private lateinit var timerCurrentState : TextView
    private lateinit var skipTimerBtn : LinearLayout
    private lateinit var timerNumbersLayout : ConstraintLayout
    private lateinit var goPreviousStateBtn : ImageView
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

    private lateinit var dataManager: DataManager
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
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        dataManager = DataManager()
        workout = intent?.getSerializableExtra("selected_workout") as WorkoutsModelClass
        timerName = findViewById(R.id.timerNameTxt)
        timerMinutes = findViewById(R.id.timerMinTxt)
        timerSeconds = findViewById(R.id.timerSecTxt)
        timerSets = findViewById(R.id.timerSetsTxt)
        timerNext = findViewById(R.id.timerNextTxt)
        startingLayout = findViewById(R.id.startingLayout)
        pausedLayout = findViewById(R.id.pausedLayout)
        startingLbl = findViewById(R.id.startingLbl)
        secsToStart = findViewById(R.id.secsToStart)
        timerLayout = findViewById(R.id.timerLayout)
        timerImage = findViewById(R.id.timerImage)
        minutesTimerLbl = findViewById(R.id.minutesTimerLbl)
        secondsTimerLbl = findViewById(R.id.secondsTimerLbl)
        repsTimerLbl = findViewById(R.id.repsTimerLbl)
        timerCurrentState = findViewById(R.id.timerCurrentState)
        skipTimerBtn = findViewById(R.id.skipTimerBtn)
        timerNumbersLayout = findViewById(R.id.timerNumbersLayout)
        layoutContainer = findViewById(R.id.layoutContainer)
        goPreviousStateBtn = findViewById(R.id.goPreviousStateBtn)
        reps = workout.num_reps
        work = workout.work_time
        rest = workout.rest_time
        numSets = workout.sets

        // Hiding and showing views according to workout mode and setting values
        if (workout.reps) { //Working with Reps
            initialWorkTime = ((workout.num_reps * workout.reps_time) * 1000 + 1000).toLong() //Set Timer Value
            workFormat = reps.toString() + " " + getString(R.string.workingRepsTimer)
            timerMinutes.visibility = View.GONE
            minutesTimerLbl.visibility = View.GONE
            secondsTimerLbl.visibility = View.GONE
            timerSeconds.textSize = 200F
        } else { //Working with Time
            initialWorkTime = (workout.work_time * 1000 + 1000).toLong() //Set Timer Value
            val workTimeFormat = convertTime(work)
            workFormat = getString(R.string.workTimer) + " " + workTimeFormat[0] + ":" + workTimeFormat[1]
            timerMinutes.text = workTimeFormat[0]
            timerSeconds.text = workTimeFormat[1]
        }
        val restTimeFormat = convertTime(rest)

        //Assigning values and assets
        restFormat = getString(R.string.restTimer) + " " + restTimeFormat[0] + ":" + restTimeFormat[1]
        timerNext.text = restFormat
        timerSets.text = currentSet.toString()
        initialRestTime = (workout.rest_time * 1000 + 1000).toLong()
        timerName.text = workout.name.uppercase()
        when (workout.category){
            "Chest" -> {timerImage.setImageResource(R.drawable.chest_ic)}
            "Back" -> {timerImage.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {timerImage.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {timerImage.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {timerImage.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {timerImage.setImageResource(R.drawable.abs_ic)}
        }
        startingLayout.visibility = View.GONE
        goPreviousStateBtn.visibility = View.GONE
        started = false

        startingTimer(initialWorkTime) //Start Short Timer Before Starting

        skipTimerBtn.setOnClickListener {
            if (started) {
                touchedSkipOnce++
                Handler(Looper.getMainLooper()).postDelayed({ //Double tap handler
                    if (touchedSkipOnce == 1) {
                        Toast.makeText(this, getString(R.string.doubleTapSkip), Toast.LENGTH_SHORT)
                            .show()
                    } else if (touchedSkipOnce == 2) {
                        val animateNumbersLayout =
                            ObjectAnimator.ofFloat(timerNumbersLayout, "alpha", 0f).apply {
                                duration = 1000
                                start()
                            }
                        animateNumbersLayout.doOnEnd {
                            skipCurrentState()
                            ObjectAnimator.ofFloat(timerNumbersLayout, "alpha", 1f).apply {
                                duration = 1000
                                start()
                            }
                        }
                        val animateText =
                            ObjectAnimator.ofFloat(timerCurrentState, "alpha", 0f).apply {
                                duration = 1000
                                start()
                            }
                        animateText.doOnEnd {
                            ObjectAnimator.ofFloat(timerCurrentState, "alpha", 1f).apply {
                                duration = 1000
                                start()
                            }
                        }
                    }
                    touchedSkipOnce = 0
                }, 200)
            }
        }

            goPreviousStateBtn.setOnClickListener {
                if (started) {
                    touchedReturnOnce++
                    Handler(Looper.getMainLooper()).postDelayed({ //Double tap handler
                        if (touchedReturnOnce == 1) {
                            Toast.makeText(this, getString(R.string.doubleTapReturn), Toast.LENGTH_SHORT).show()
                        } else if (touchedReturnOnce == 2) {
                            val animateNumbersLayout =
                                ObjectAnimator.ofFloat(timerNumbersLayout, "alpha", 0f).apply {
                                    duration = 1000
                                    start()
                                }
                            animateNumbersLayout.doOnEnd {
                                returnPreviousState()
                                ObjectAnimator.ofFloat(timerNumbersLayout, "alpha", 1f).apply {
                                    duration = 1000
                                    start()
                                }
                            }
                            val animateText =
                                ObjectAnimator.ofFloat(timerCurrentState, "alpha", 0f).apply {
                                    duration = 1000
                                    start()
                                }
                            animateText.doOnEnd {
                                ObjectAnimator.ofFloat(timerCurrentState, "alpha", 1f).apply {
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
        timerLayout.setOnClickListener {
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
            },300)
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
        if (currentSet == 1 && resting){
            goPreviousStateBtn.visibility = View.VISIBLE
        } else if (currentSet > 1){
            goPreviousStateBtn.visibility = View.VISIBLE
        }else {
            goPreviousStateBtn.visibility = View.GONE
        }
        startingLayout.visibility = View.VISIBLE
        timerLayout.visibility = View.GONE
        countDownInPause = initialWorkTime
        countDownInterval = 1000L
        startingLayout.setBackgroundColor(resources.getColor(R.color.timerGrayLightStarting))
        startDownTimer = object : CountDownTimer(6000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                countDownInPause = initialWorkTime
                actionOnIntervalStarting((millisUntilFinished))
                actionOnIntervalStarting((countDownInterval))
                if ((millisUntilFinished/countDownInterval) > 3){
                    startingLbl.text = getString(R.string.getReady)
                    secsToStart.visibility = View.GONE
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                } else if ((millisUntilFinished/countDownInterval) <= 3 && (millisUntilFinished/countDownInterval) > 0.99){
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                    startingLbl.text = getString(R.string.starting)
                    secsToStart.text = (millisUntilFinished/countDownInterval).toString()
                    secsToStart.visibility = View.VISIBLE
                } else if ((millisUntilFinished/countDownInterval) < 1){
                    actionOnIntervalStarting((millisUntilFinished/countDownInterval))
                    secsToStart.text = getString(R.string.timerStartingFinalValue)
                }
            }
            override fun onFinish() {
                started = true
                startWorkTimer(time) //Start Workout timer after this is finished
                startingLayout.visibility = View.GONE
                timerLayout.visibility = View.VISIBLE
            }
        }
        startDownTimer.start() //Start Short Timer
    }

    private fun actionOnIntervalStarting(num: Long){ //actions to do during workout

    }

    private fun startWorkTimer(time : Long){ //Workout Timer
        timerNext.text = restFormat
        timerSets.text = (currentSet).toString()
        timerCurrentState.text = getString(R.string.timerWorkTxt)
        if (workout.reps){ //Working with Reps, Set Visibility
            countDownInterval = (workout.reps_time * 1000).toLong()
            timerMinutes.visibility = View.GONE
            minutesTimerLbl.visibility = View.GONE
            secondsTimerLbl.visibility = View.GONE
            repsTimerLbl.visibility = View.VISIBLE
            timerSeconds.textSize = 200F
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    timerSeconds.text = (millisUntilFinished/countDownInterval).toString()
                }
                override fun onFinish() {
                    startRestTimer(initialRestTime) //Start rest time after this is finished
                }
            }
            countDownTimer.start() //Start Reps Timer
        } else{ //Working with Time, Set Visibility
            countDownInterval = 1000
            repsTimerLbl.visibility = View.GONE
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    work = (millisUntilFinished/1000).toInt()
                    val timeFormat = convertTime(work)
                    timerMinutes.text = timeFormat[0]
                    timerSeconds.text = timeFormat[1]
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
                if ((millisUntilFinished/countDownInterval) < 3 && (millisUntilFinished/countDownInterval)>= 1){
                    actionOnInterval(true)
                } else if (millisUntilFinished/countDownInterval < 1){
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
        if (currentSet == 1 && resting){
            goPreviousStateBtn.visibility = View.VISIBLE
        } else if (currentSet > 1){
            goPreviousStateBtn.visibility = View.VISIBLE
        }else {
            goPreviousStateBtn.visibility = View.GONE
        }
        timerSets.text = (currentSet).toString()
        timerCurrentState.text = getString(R.string.timerRestTxt)
        timerSeconds.textSize = 150F
        if (numSets > 1) {
            timerNext.text = workFormat
        } else{
            timerNext.text = getString(R.string.end)
        }
        timerMinutes.visibility = View.VISIBLE
        minutesTimerLbl.visibility = View.VISIBLE
        secondsTimerLbl.visibility = View.VISIBLE
        repsTimerLbl.visibility = View.GONE
        timerSeconds.marginStart.plus(60)
        countDownInterval = 1000
        countDownTimer = object : CountDownTimer(time, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                countDownInPause = millisUntilFinished
                rest = (millisUntilFinished/1000).toInt()
                val timeFormat = convertTime(rest)
                timerMinutes.text = timeFormat[0]
                timerSeconds.text = timeFormat[1]
            }
            override fun onFinish() {
                resting = false
                numSets--
                currentSet++
                if (numSets > 0){ //Sets is greater than 0
                    startingTimer(initialWorkTime) //Start Workout Timer
                } else { //Sets is 0
                    launchHome()//Launch home Screen
                }
            }
        }
        countDownTimer.start() //Start rest Timer
    }


    private fun actionOnInterval(isFinishing : Boolean){ //actions to do during workout
    //Create sound player for interval Ticks
            val resID = resources.getIdentifier("tick", "raw", packageName)
            mediaPlayer.reset()
            mediaPlayer = MediaPlayer.create(this, resID)
            mediaPlayer.start()
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
        pausedLayout.visibility =View.VISIBLE
        countDownTimer.cancel()
        if (started){timerInterval.cancel()}
        isPaused = true
    }

    private fun resumeTimer(){
        pausedLayout.visibility =View.GONE
        if (resting) {
            startRestTimer(countDownInPause)
        } else {
            startingTimer(countDownInPause)
        }
        isPaused = false
    }

    private fun convertTime(secs: Int): Array<String> {
        val minutes = secs / 60
        val seconds = secs % 60
        val minutesTxt: String = if (minutes < 10){
            "0$minutes"
        } else {
            "$minutes"
        }
        val secondsTxt: String = if (seconds < 10){
            "0$seconds"
        } else {
            "$seconds"
        }
        return arrayOf(minutesTxt, secondsTxt)
    }


}

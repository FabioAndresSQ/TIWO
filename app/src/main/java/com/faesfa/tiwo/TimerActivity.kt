package com.faesfa.tiwo

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout

class TimerActivity : AppCompatActivity() {
    //Initialize everything
    private lateinit var timerName : TextView
    private lateinit var timerTitle : TextView
    private lateinit var timerSets : TextView
    private lateinit var timerMinutes : TextView
    private lateinit var timerSeconds : TextView
    private lateinit var timerDivisor : TextView
    private lateinit var timerNext : TextView
    private lateinit var timerLayout : ConstraintLayout
    private lateinit var startingLayout : ConstraintLayout
    private lateinit var secsToStart : TextView
    private lateinit var workout : WorkoutsModelClass
    private var numSets = 0
    private var reps = 0
    private var work = 0
    private var rest = 0
    private var isPaused = false
    private var started = false
    private var resting = false
    private var backPressedOnce = false
    private var touchedOnce = 0
    private var showedFirst = false

    private lateinit var dataManager: DataManager
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerInterval: CountDownTimer
    private var initialWorkTime: Long = 0
    private var initialRestTime: Long = 0
    private var countDownInterval: Long = 1000
    private var countDownInPause: Long = 0
    private var restFormat: String = ""
    private var workFormat: String = ""
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
        timerTitle = findViewById(R.id.timerTittleTxt)
        timerMinutes = findViewById(R.id.timerMinTxt)
        timerDivisor = findViewById(R.id.divisor)
        timerSeconds = findViewById(R.id.timerSecTxt)
        timerSets = findViewById(R.id.timerSetsTxt)
        timerNext = findViewById(R.id.timerNextTxt)
        startingLayout = findViewById(R.id.startingLayout)
        secsToStart = findViewById(R.id.secsToStart)
        timerLayout = findViewById(R.id.timerLayout)
        reps = workout.num_reps
        work = workout.work_time
        rest = workout.rest_time
        numSets = workout.sets
        if (workout.reps) { //Working with Reps
            initialWorkTime = ((workout.num_reps * workout.reps_time) * 1000 + 1000).toLong() //Set Timer Value
            workFormat = reps.toString() + " " + getString(R.string.workingRepsTimer)
            timerMinutes.visibility = View.GONE
            timerDivisor.visibility = View.GONE
        } else { //Working with Time
            initialWorkTime = (workout.work_time * 1000 + 1000).toLong() //Set Timer Value
            val workTimeFormat = convertTime(work)
            workFormat = getString(R.string.workTimer) + " " + workTimeFormat[0] + ":" + workTimeFormat[1]
            timerMinutes.text = workTimeFormat[0]
            timerSeconds.text = workTimeFormat[1]
        }
        val restTimeFormat = convertTime(rest)
        restFormat = getString(R.string.restTimer) + " " + restTimeFormat[0] + ":" + restTimeFormat[1]
        timerNext.text = restFormat
        timerSets.text = numSets.toString()
        initialRestTime = (workout.rest_time * 1000 + 1000).toLong()
        timerName.text = workout.name
        startingLayout.visibility = View.GONE
        started = false
        startingTimer(initialWorkTime) //Start Short Timer Before Starting

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

    private fun startingTimer(time: Long) { //Short timer before Starting Workout
        startingLayout.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(3000, 950){
            override fun onTick(millisUntilFinished: Long) {
                countDownInPause = initialWorkTime
                secsToStart.text = (millisUntilFinished/countDownInterval).toString()
            }
            override fun onFinish() {
                started = true
                startingLayout.visibility = View.GONE
                startWorkTimer(time) //Start Workout timer after this is finished
            }
        }
        countDownTimer.start() //Start Short Timer
    }

    private fun startWorkTimer(time : Long){ //Workout Timer
        timerNext.text = restFormat
        timerSets.text = (numSets-1).toString()
        if (workout.reps){ //Working with Reps, Set Visibility
            countDownInterval = (workout.reps_time * 1000).toLong()
            timerTitle.text = getString(R.string.workingRepsTimer)
            timerMinutes.visibility = View.GONE
            timerDivisor.visibility = View.GONE
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    timerSeconds.text = (millisUntilFinished/countDownInterval).toString()
                }
                override fun onFinish() {
                    numSets--
                    startRestTimer(initialRestTime) //Start rest time after this is finished
                }
            }
            countDownTimer.start() //Start Reps Timer
        } else{ //Working with Time, Set Visibility
            countDownInterval = 1000
            timerTitle.text = getString(R.string.workingTimeTimer)
            countDownTimer = object : CountDownTimer(time, countDownInterval){
                override fun onTick(millisUntilFinished: Long) {
                    countDownInPause = millisUntilFinished
                    work = (millisUntilFinished/1000).toInt()
                    val timeFormat = convertTime(work)
                    timerMinutes.text = timeFormat[0]
                    timerSeconds.text = timeFormat[1]
                }
                override fun onFinish() {
                    numSets--
                    startRestTimer(initialRestTime) //Start rest time after this is finished
                }

            }
            countDownTimer.start() //Start Time Timer
        }

        //Timer Background to do things in between
        timerInterval = object  : CountDownTimer(time, countDownInterval/2){
            override fun onTick(millisUntilFinished: Long) {
                actionOnInterval("UP", "DOWN")
            }
            override fun onFinish() {
                showedFirst = false
            }
        }
        timerInterval.start() //Start Timer Background

    }

    private fun startRestTimer(time : Long){ //Rest Timer
        resting = true
        if (numSets > 0) {
            timerNext.text = workFormat
        } else{
            timerNext.text = getString(R.string.end)
        }
        timerMinutes.visibility = View.VISIBLE
        timerDivisor.visibility = View.VISIBLE
        countDownInterval = 1000
        timerTitle.text = getString(R.string.restingTimeTimer)
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
                if (numSets > 0){ //Sets is greater than 0
                    startingTimer(initialWorkTime) //Start Workout Timer
                } else { //Sets is 0
                    launchHome()//Launch home Screen
                }
            }
        }
        countDownTimer.start() //Start rest Timer
    }

    private fun actionOnInterval(txt1 :String , txt2 :String){ //actions to do during workout
        showedFirst = if (!showedFirst){
            //Toast.makeText(this, txt1, Toast.LENGTH_SHORT).show()
            timerLayout.setBackgroundColor(resources.getColor(R.color.teal_200))
            true
        } else {
            //Toast.makeText(this, txt2, Toast.LENGTH_SHORT).show()
            timerLayout.setBackgroundColor(resources.getColor(R.color.white))
            false
        }

    }

    override fun onBackPressed() { //Handle back pressed
        if (backPressedOnce) {//Double pressed
            launchHome()
            countDownTimer.cancel()
            timerInterval.cancel()
        }
        this.backPressedOnce = true
        Toast.makeText(this, getString(R.string.backToExitTimer), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce=false },2000)
    }

    private fun launchHome(){//Launch home screen
        val launchHome = Intent(this, MainActivity::class.java)
        startActivity(launchHome)
    }

    private fun pauseTimer(){
        countDownTimer.cancel()
        if (started){timerInterval.cancel()}
        isPaused = true
    }

    private fun resumeTimer(){
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

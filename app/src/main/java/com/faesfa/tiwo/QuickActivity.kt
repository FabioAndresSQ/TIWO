package com.faesfa.tiwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import java.io.Serializable

class QuickActivity : AppCompatActivity() {

    private lateinit var quickWorking : Switch
    private lateinit var quickSetsTxt : TextView
    private lateinit var quickRemoveSetsBtn : ImageButton
    private lateinit var quickAddSetsBtn : ImageButton
    private lateinit var quickNumRepsTitle : TextView
    private lateinit var quickRepsTxt : TextView
    private lateinit var quickRemoveRepsBtn : ImageButton
    private lateinit var quickAddRepsBtn : ImageButton
    private lateinit var quickRepsIntervalTitle : TextView
    private lateinit var quickIntervalTxt : TextView
    private lateinit var quickRemoveIntervalBtn : ImageButton
    private lateinit var quickAddIntervalBtn : ImageButton
    private lateinit var quickSecondsIntervalTxt : TextView
    private lateinit var quickWorkTimeTitle : TextView
    private lateinit var workMinutesQuick : NumberPicker
    private lateinit var workSecondsQuick : NumberPicker
    private lateinit var dotsWorkTimeQuick : TextView
    private lateinit var restMinutesQuick : NumberPicker
    private lateinit var restSecondsQuick : NumberPicker
    private lateinit var quickStartBtn : Button
    private lateinit var workout : WorkoutsModelClass
    private var sets = 3 //Default sets 3
    private var reps = 10
    private var interval = 1.5
    private var workMinutes = 0
    private var workSeconds = 0
    private var restMinutes = 0
    private var restSeconds = 0
    private lateinit var toolBar : Toolbar
    private lateinit var helpBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick)

        toolBar = findViewById(R.id.includeQuickBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        helpBtn = findViewById(R.id.infoQuick)

        helpBtn.setOnClickListener {
            //Show Help Info
            Toast.makeText(this, "Showing Help", Toast.LENGTH_SHORT).show()
        }

        quickWorking = findViewById(R.id.quickWorking)
        quickSetsTxt = findViewById(R.id.quickSetsTxt)
        quickRemoveSetsBtn = findViewById(R.id.quickRemoveSetsBtn)
        quickAddSetsBtn = findViewById(R.id.quickAddSetsBtn)
        quickNumRepsTitle = findViewById(R.id.quickNumRepsTitle)
        quickRepsTxt = findViewById(R.id.quickRepsTxt)
        quickRemoveRepsBtn = findViewById(R.id.quickRemoveRepsBtn)
        quickAddRepsBtn = findViewById(R.id.quickAddRepsBtn)
        quickRepsIntervalTitle = findViewById(R.id.quickRepsIntervalTitle)
        quickIntervalTxt = findViewById(R.id.quickIntervalTxt)
        quickRemoveIntervalBtn = findViewById(R.id.quickRemoveIntervalBtn)
        quickAddIntervalBtn = findViewById(R.id.quickAddIntervalBtn)
        quickSecondsIntervalTxt = findViewById(R.id.quickSecondsIntervalTxt)
        quickWorkTimeTitle = findViewById(R.id.quickWorkTimeTitle)
        workMinutesQuick = findViewById(R.id.workMinutesQuick)
        workSecondsQuick = findViewById(R.id.workSecondsQuick)
        dotsWorkTimeQuick = findViewById(R.id.dotsWorkTimeQuick)
        restMinutesQuick = findViewById(R.id.restMinutesQuick)
        restSecondsQuick = findViewById(R.id.restSecondsQuick)
        quickStartBtn = findViewById(R.id.quickStartBtn)

        //Listener for Switch
        changeVisibility(quickWorking.isChecked)
        quickWorking.setOnCheckedChangeListener { buttonView, isChecked ->
            changeVisibility(isChecked)
        }

        //Set listener to sets add or remove
        quickSetsTxt.text = sets.toString()
        quickAddSetsBtn.setOnClickListener {
            if (sets > 0) {
                sets++
                quickSetsTxt.text = sets.toString()
                quickRemoveSetsBtn.visibility = View.VISIBLE
            }
        }
        quickRemoveSetsBtn.setOnClickListener {
            if (sets > 1){
                sets--
                quickSetsTxt.text = sets.toString()
            } else if (sets == 1) {
                quickRemoveSetsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Reps
        quickAddRepsBtn.setOnClickListener {
            if (reps > 0) {
                reps++
                quickRepsTxt.text = reps.toString()
                quickRemoveRepsBtn.visibility = View.VISIBLE
            }
        }
        quickRemoveRepsBtn.setOnClickListener {
            if (reps > 1){
                reps--
                quickRepsTxt.text = reps.toString()
            } else if (reps == 1) {
                quickRemoveRepsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Interval
        quickAddIntervalBtn.setOnClickListener {
            if (interval > 0.0) {
                interval += 0.5
                quickIntervalTxt.text = interval.toString()
                quickRemoveIntervalBtn.visibility = View.VISIBLE
            }
        }
        quickRemoveIntervalBtn.setOnClickListener {
            if (interval > 0.6){
                interval -= 0.5
                quickIntervalTxt.text = interval.toString()
            } else if (interval == 0.5) {
                quickRemoveIntervalBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for the NumberPickers on Work Time
        workMinutesQuick.maxValue = 59
        workMinutesQuick.minValue = 0
        workSecondsQuick.maxValue = 59
        workSecondsQuick.minValue = 0
        workMinutesQuick.setOnValueChangedListener { picker, oldVal, newVal ->
            workMinutes = newVal
        }
        workSecondsQuick.setOnValueChangedListener { picker, oldVal, newVal ->
            workSeconds = newVal
        }

        //Listener for the NumberPickers on Rest Time
        restMinutesQuick.maxValue = 59
        restMinutesQuick.minValue = 0
        restSecondsQuick.maxValue = 59
        restSecondsQuick.minValue = 0
        restMinutesQuick.setOnValueChangedListener { picker, oldVal, newVal ->
            restMinutes = newVal
        }
        restSecondsQuick.setOnValueChangedListener { picker, oldVal, newVal ->
            restSeconds = newVal
        }

        //listener for Start Quick timer Btn
        quickStartBtn.setOnClickListener {
            val workTime = (workMinutes * 60) + workSeconds
            val restTime = (restMinutes * 60) + restSeconds
            if (quickWorking.isChecked){ //Working with reps
                if (reps > 0){ //Checks for Number of reps to not be 0
                    if (interval > 0){ //Checks for reps interval to not be 0
                        if (restTime > 0){ //Checks that Rest is not 0
                            if (sets > 0) {
                                //Assign values to workout Obj
                                workout  = WorkoutsModelClass("Quick Timer", sets, true,reps,interval,0,restTime, "")
                                //Launch Timer Activity with workout
                                val launchTimer = Intent(this, TimerActivity::class.java)
                                launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
                                startActivity(launchTimer)
                            } else {
                                Toast.makeText(this, "Sets Time Can't be 0", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Rest Can't be 0", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "Interval Can't be 0", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Reps Can't be 0", Toast.LENGTH_SHORT).show()
                }
            } else { //Working with Time
                if (workTime > 0){ //Checks for work to not be 0
                    if (restTime > 0){ //Checks for rest to not be 0
                        if (sets > 0) {
                            //Assign values to workout Obj
                            workout  = WorkoutsModelClass("Quick Timer", sets, false,0,0.0,workTime,restTime, "")
                            //Launch Timer Activity with workout
                            val launchTimer = Intent(this, TimerActivity::class.java)
                            launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
                            startActivity(launchTimer)
                        } else {
                            Toast.makeText(this, "Sets Time Can't be 0", Toast.LENGTH_SHORT).show()
                        }
                    } else{
                        Toast.makeText(this, "Rest Time Can't be 0", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this, "Work Time Can't be 0", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun changeVisibility(workReps : Boolean){ //Set visibility depending on working with reps or time
        if (!workReps){ //Working time
            quickNumRepsTitle.visibility = View.GONE
            quickRepsTxt.visibility = View.GONE
            quickAddRepsBtn.visibility = View.GONE
            quickRemoveRepsBtn.visibility = View.GONE

            quickRepsIntervalTitle.visibility = View.GONE
            quickIntervalTxt.visibility = View.GONE
            quickAddIntervalBtn.visibility = View.GONE
            quickRemoveIntervalBtn.visibility = View.GONE
            quickSecondsIntervalTxt.visibility = View.GONE

            quickWorkTimeTitle.visibility = View.VISIBLE
            workMinutesQuick.visibility = View.VISIBLE
            workSecondsQuick.visibility = View.VISIBLE
            dotsWorkTimeQuick.visibility = View.VISIBLE
            Toast.makeText(this, "Working with Time", Toast.LENGTH_SHORT).show()
        } else { //Working reps
            quickNumRepsTitle.visibility = View.VISIBLE
            quickRepsTxt.visibility = View.VISIBLE
            quickAddRepsBtn.visibility = View.VISIBLE
            quickRemoveRepsBtn.visibility = View.VISIBLE

            quickRepsIntervalTitle.visibility = View.VISIBLE
            quickIntervalTxt.visibility = View.VISIBLE
            quickAddIntervalBtn.visibility = View.VISIBLE
            quickRemoveIntervalBtn.visibility = View.VISIBLE
            quickSecondsIntervalTxt.visibility = View.VISIBLE

            quickWorkTimeTitle.visibility = View.GONE
            workMinutesQuick.visibility = View.GONE
            workSecondsQuick.visibility = View.GONE
            dotsWorkTimeQuick.visibility = View.GONE
            Toast.makeText(this, "Working with Reps", Toast.LENGTH_SHORT).show()
        }
    }

}
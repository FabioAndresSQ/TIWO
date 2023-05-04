package com.faesfa.tiwo

import android.R.color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import java.lang.reflect.Field


class CreateFragment2 : Fragment() {
    //Initialize all views on Screen
    private lateinit var numRepsTitle: TextView
    private lateinit var createRepsTxt: TextView
    private lateinit var addRepsBtn: ImageButton
    private lateinit var removeRepsBtn: ImageButton
    private lateinit var repsIntervalTitle: TextView
    private lateinit var createIntervalTxt: TextView
    private lateinit var addIntervalBtn: ImageButton
    private lateinit var removeIntervalBtn: ImageButton
    private lateinit var secondsIntervalTxt: TextView
    private lateinit var workTimeTitle: TextView
    private lateinit var workMinutesCreate: NumberPicker
    private lateinit var workSecondsCreate: NumberPicker
    private lateinit var dotsWorkTime: TextView
    private lateinit var restTimeTitle: TextView
    private lateinit var restMinutesCreate: NumberPicker
    private lateinit var restSecondsCreate: NumberPicker
    private lateinit var workout : WorkoutsModelClass

    private lateinit var nextBtn : LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = this.arguments
        workout = args?.getSerializable("workout") as WorkoutsModelClass //Receive data from Prev Fragment
        val view = inflater.inflate(R.layout.fragment_create2, container, false) //save inflater into view
        var reps = 10
        var interval = 1.5
        var workMinutes = 0
        var workSeconds = 0
        var restMinutes = 0
        var restSeconds = 0

        //Declare each view
        numRepsTitle = view.findViewById(R.id.numRepsTitle)
        createRepsTxt = view.findViewById(R.id.createRepsTxt)
        addRepsBtn = view.findViewById(R.id.addRepsBtn)
        removeRepsBtn = view.findViewById(R.id.removeRepsBtn)
        repsIntervalTitle = view.findViewById(R.id.repsIntervalTitle)
        createIntervalTxt = view.findViewById(R.id.createIntervalTxt)
        addIntervalBtn = view.findViewById(R.id.addIntervalBtn)
        removeIntervalBtn = view.findViewById(R.id.removeIntervalBtn)
        secondsIntervalTxt = view.findViewById(R.id.secondsIntervalTxt)
        workTimeTitle = view.findViewById(R.id.workTimeTitle)
        workMinutesCreate = view.findViewById(R.id.workMinutesCreate)
        workSecondsCreate = view.findViewById(R.id.workSecondsCreate)
        dotsWorkTime = view.findViewById(R.id.dotsWorkTime)
        restTimeTitle = view.findViewById(R.id.restTimeTitle)
        restMinutesCreate = view.findViewById(R.id.restMinutesCreate)
        restSecondsCreate = view.findViewById(R.id.restSecondsCreate)
        nextBtn = view.findViewById(R.id.createNext2)

        //Check if working with Reps or Time and Set Visibility
        changeVisibility(workout.reps)

        if (workout.reps) { //Set Listeners for Buttons if working with Reps
            addRepsBtn.setOnClickListener {
                if (reps > 0) {
                    reps++
                    createRepsTxt.text = reps.toString()
                    removeRepsBtn.visibility = View.VISIBLE
                }
            }
            removeRepsBtn.setOnClickListener {
                if (reps > 1){
                    reps--
                    createRepsTxt.text = reps.toString()
                } else if (reps == 1) {
                    removeRepsBtn.visibility = View.INVISIBLE
                }
            }

            //Set Listeners for Buttons on Interval
            addIntervalBtn.setOnClickListener {
                if (interval > 0.0) {
                    interval += 0.5
                    createIntervalTxt.text = interval.toString()
                    removeIntervalBtn.visibility = View.VISIBLE
                }
            }
            removeIntervalBtn.setOnClickListener {
                if (interval > 0.6){
                    interval -= 0.5
                    createIntervalTxt.text = interval.toString()
                } else if (interval == 0.5) {
                    removeIntervalBtn.visibility = View.INVISIBLE
                }
            }
        } else { // Set listeners if Working with Time
            //Listener for the NumberPickers on Work Time
            workMinutesCreate.maxValue = 59
            workMinutesCreate.minValue = 0
            workSecondsCreate.maxValue = 59
            workSecondsCreate.minValue = 0
            workMinutesCreate.setOnValueChangedListener { picker, oldVal, newVal ->
                workMinutes = newVal
            }
            workSecondsCreate.setOnValueChangedListener { picker, oldVal, newVal ->
                workSeconds = newVal
            }
        }
        //Listener for the NumberPickers on Rest Time
        restMinutesCreate.maxValue = 59
        restMinutesCreate.minValue = 0
        restSecondsCreate.maxValue = 59
        restSecondsCreate.minValue = 0
        restMinutesCreate.setOnValueChangedListener { picker, oldVal, newVal ->
            restMinutes = newVal
        }
        restSecondsCreate.setOnValueChangedListener { picker, oldVal, newVal ->
            restSeconds = newVal
        }

        //Listener for Next Button
        nextBtn.setOnClickListener {
            val workTime = (workMinutes * 60) + workSeconds
            val restTime = (restMinutes * 60) + restSeconds
            if (workout.reps){ //Working with reps
                if (reps > 0){ //Checks for Number of reps to not be 0
                    if (interval > 0){ //Checks for reps interval to not be 0
                        if (restTime > 0){ //Checks that Rest is not 0
                            workout.num_reps = reps
                            workout.reps_time = interval
                            workout.work_time = 0
                            workout.rest_time = restTime
                            //Launch next Fragment and set Bundle
                            val bundle = Bundle()
                            bundle.putSerializable("workout", workout)
                            view.findNavController().navigate(R.id.createFragment3, bundle)
                        } else {
                            Toast.makeText(this.context, "Rest Can't be 0:0", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this.context, "Interval Can't be 0", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this.context, "Reps Can't be 0", Toast.LENGTH_SHORT).show()
                }
            } else { //Working with Time
                if (workTime > 0){ //Checks for work to not be 0
                    if (restTime > 0){ //Checks for rest to not be 0
                        //Assign values to workout Obj
                        workout.num_reps = 0
                        workout.reps_time = 0.0
                        workout.work_time = workTime
                        workout.rest_time = restTime
                        //Launch next Fragment and set Bundle
                        val bundle = Bundle()
                        bundle.putSerializable("workout", workout)
                        view.findNavController().navigate(R.id.createFragment3, bundle)
                    } else{
                        Toast.makeText(this.context, "Rest Time Can't be 0:0", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this.context, "Work Time Can't be 0:0", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    private fun changeVisibility(workReps : Boolean){ //Set visibility depending on working with reps or time
        if (!workReps){ //Working time
            numRepsTitle.visibility = View.GONE
            createRepsTxt.visibility = View.GONE
            addRepsBtn.visibility = View.GONE
            removeRepsBtn.visibility = View.GONE

            repsIntervalTitle.visibility = View.GONE
            createIntervalTxt.visibility = View.GONE
            addIntervalBtn.visibility = View.GONE
            removeIntervalBtn.visibility = View.GONE
            secondsIntervalTxt.visibility = View.GONE

            workTimeTitle.visibility = View.VISIBLE
            workMinutesCreate.visibility = View.VISIBLE
            workSecondsCreate.visibility = View.VISIBLE
            dotsWorkTime.visibility = View.VISIBLE
        } else { //Working reps
            numRepsTitle.visibility = View.VISIBLE
            createRepsTxt.visibility = View.VISIBLE
            addRepsBtn.visibility = View.VISIBLE
            removeRepsBtn.visibility = View.VISIBLE

            repsIntervalTitle.visibility = View.VISIBLE
            createIntervalTxt.visibility = View.VISIBLE
            addIntervalBtn.visibility = View.VISIBLE
            removeIntervalBtn.visibility = View.VISIBLE
            secondsIntervalTxt.visibility = View.VISIBLE

            workTimeTitle.visibility = View.GONE
            workMinutesCreate.visibility = View.GONE
            workSecondsCreate.visibility = View.GONE
            dotsWorkTime.visibility = View.GONE
        }
    }
}
package com.faesfa.tiwo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.faesfa.tiwo.databinding.FragmentCreate2Binding


class CreateFragment2 : Fragment() {
    //Initialize all views on Screen
    private var _binding: FragmentCreate2Binding? = null
    private val binding get() = _binding!!

    private lateinit var workout : WorkoutsModelClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = this.arguments
        workout = args?.getSerializable("workout") as WorkoutsModelClass //Receive data from Prev Fragment

        _binding = FragmentCreate2Binding.inflate(inflater, container,false)
        val view = binding.root //save inflater into view

        var reps = 10
        var interval = 1.5
        var workMinutes = 0
        var workSeconds = 0
        var restMinutes = 0
        var restSeconds = 0

        //Check if working with Reps or Time and Set Visibility
        changeVisibility(workout.reps)

        if (workout.reps) { //Set Listeners for Buttons if working with Reps
            binding.addRepsBtn.setOnClickListener {
                if (reps > 0) {
                    reps++
                    binding.createRepsTxt.text = reps.toString()
                    binding.removeRepsBtn.visibility = View.VISIBLE
                }
            }
            binding.removeRepsBtn.setOnClickListener {
                if (reps > 1){
                    reps--
                    binding.createRepsTxt.text = reps.toString()
                } else if (reps == 1) {
                    binding.removeRepsBtn.visibility = View.INVISIBLE
                }
            }

            //Set Listeners for Buttons on Interval
            binding.addIntervalBtn.setOnClickListener {
                if (interval > 0.0) {
                    interval += 0.5
                    binding.createIntervalTxt.text = interval.toString()
                    binding.removeIntervalBtn.visibility = View.VISIBLE
                }
            }
            binding.removeIntervalBtn.setOnClickListener {
                if (interval > 0.6){
                    interval -= 0.5
                    binding.createIntervalTxt.text = interval.toString()
                } else if (interval == 0.5) {
                    binding.removeIntervalBtn.visibility = View.INVISIBLE
                }
            }
        } else { // Set listeners if Working with Time
            //Listener for the NumberPickers on Work Time
            binding.workMinutesCreate.maxValue = 59
            binding.workMinutesCreate.minValue = 0
            binding.workSecondsCreate.maxValue = 59
            binding.workSecondsCreate.minValue = 0
            binding.workMinutesCreate.setOnValueChangedListener { _, _, newVal ->
                workMinutes = newVal
            }
            binding.workSecondsCreate.setOnValueChangedListener { _, _, newVal ->
                workSeconds = newVal
            }
        }
        //Listener for the NumberPickers on Rest Time
        binding.restMinutesCreate.maxValue = 59
        binding.restMinutesCreate.minValue = 0
        binding.restSecondsCreate.maxValue = 59
        binding.restSecondsCreate.minValue = 0
        binding.restMinutesCreate.setOnValueChangedListener { _, _, newVal ->
            restMinutes = newVal
        }
        binding.restSecondsCreate.setOnValueChangedListener { _, _, newVal ->
            restSeconds = newVal
        }

        //Listener for Next Button
        binding.createNext2.setOnClickListener {
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
            binding.numRepsTitle.visibility = View.GONE
            binding.createRepsTxt.visibility = View.GONE
            binding.addRepsBtn.visibility = View.GONE
            binding.removeRepsBtn.visibility = View.GONE

            binding.repsIntervalTitle.visibility = View.GONE
            binding.createIntervalTxt.visibility = View.GONE
            binding.addIntervalBtn.visibility = View.GONE
            binding.removeIntervalBtn.visibility = View.GONE
            binding.secondsIntervalTxt.visibility = View.GONE

            binding.workTimeTitle.visibility = View.VISIBLE
            binding.workMinutesCreate.visibility = View.VISIBLE
            binding.workSecondsCreate.visibility = View.VISIBLE
            binding.dotsWorkTime.visibility = View.VISIBLE
        } else { //Working reps
            binding.numRepsTitle.visibility = View.VISIBLE
            binding.createRepsTxt.visibility = View.VISIBLE
            binding.addRepsBtn.visibility = View.VISIBLE
            binding.removeRepsBtn.visibility = View.VISIBLE

            binding.repsIntervalTitle.visibility = View.VISIBLE
            binding.createIntervalTxt.visibility = View.VISIBLE
            binding.addIntervalBtn.visibility = View.VISIBLE
            binding.removeIntervalBtn.visibility = View.VISIBLE
            binding.secondsIntervalTxt.visibility = View.VISIBLE

            binding.workTimeTitle.visibility = View.GONE
            binding.workMinutesCreate.visibility = View.GONE
            binding.workSecondsCreate.visibility = View.GONE
            binding.dotsWorkTime.visibility = View.GONE
        }
    }
}
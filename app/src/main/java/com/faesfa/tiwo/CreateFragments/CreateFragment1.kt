package com.faesfa.tiwo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController

// initialize the workout Model Obj
private lateinit var workout : WorkoutsModelClass
private var sets = 3 //Default sets 3

class CreateFragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create1, container, false) //save inflater to view
        //Assign every view
        val nameTxt = view.findViewById<EditText>(R.id.createNameTxt)
        val setsTxt = view.findViewById<TextView>(R.id.createSetsTxt)
        val addBtn = view.findViewById<ImageButton>(R.id.addSetsBtn)
        val removeBtn = view.findViewById<ImageButton>(R.id.removeSetsBtn)
        val workingWithTxt = view.findViewById<Switch>(R.id.createWorking)
        val nextBtn = view.findViewById<Button>(R.id.createNext1)
        //Create the workout obj with default values
        workout = WorkoutsModelClass("default", 0, false,0,0.0,0,0)


        //Set listener to sets add or remove
        setsTxt.text = sets.toString()
        addBtn.setOnClickListener {
            if (sets > 0) {
                sets++
                setsTxt.text = sets.toString()
                removeBtn?.visibility = VISIBLE
            }
        }
        removeBtn.setOnClickListener {
            if (sets > 1){
                sets--
                setsTxt.text = sets.toString()
            } else if (sets == 1) {
                removeBtn.visibility = INVISIBLE
            }
        }
        //Set listener to button to go next fragment
        nextBtn.setOnClickListener {
            if (nameTxt.text?.isNotEmpty() == true){ //Check that name fiel is not empty
                if (sets > 0) { //Check that sets is more than 0
                    //Set values to Workout Obj
                    workout.name = nameTxt.text.toString()
                    workout.sets = sets
                    workout.reps = workingWithTxt.isChecked //Check if the switch is time or Reps

                    val bundle = Bundle()
                    bundle.putSerializable("workout", workout) // Save workout on bundle to pass it to next fragment
                    view.findNavController().navigate(R.id.createFragment2, bundle) //Launch next Fragment with info
                } else{
                    Toast.makeText(this.context, "Sets can't be 0", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this.context, "Name Can't be empty", Toast.LENGTH_SHORT).show()
            }

        }
        // Inflate the layout for this fragment
        return view
    }

}


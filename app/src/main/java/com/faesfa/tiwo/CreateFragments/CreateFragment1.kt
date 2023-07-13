package com.faesfa.tiwo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.navigation.findNavController
import com.faesfa.tiwo.databinding.FragmentCreate1Binding

// initialize the workout Model Obj
private lateinit var workout : WorkoutsModelClass
private var sets = 3 //Default sets 3

class CreateFragment1 : Fragment() {
    private var _binding:FragmentCreate1Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreate1Binding.inflate(inflater, container, false)

        val view = binding.root //save inflater to view
        //Create the workout obj with default values
        workout = WorkoutsModelClass("default", 0, false,0,0.0,0,0, "")

        //Set listener to sets add or remove
        binding.createSetsTxt.text = sets.toString()
        binding.addSetsBtn.setOnClickListener {
            if (sets > 0) {
                sets++
                binding.createSetsTxt.text = sets.toString()
                binding.removeSetsBtn.visibility = VISIBLE
            }
        }
        binding.removeSetsBtn.setOnClickListener {
            if (sets > 1){
                sets--
                binding.createSetsTxt.text = sets.toString()
            } else if (sets == 1) {
                binding.removeSetsBtn.visibility = INVISIBLE
            }
        }

        binding.createNameTxt.setOnFocusChangeListener { _, b -> if (!b) {
            val keyboard = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(binding.createNameTxt.windowToken, 0)}
        }

        //Set listener to button to go next fragment
        binding.createNext1.setOnClickListener {
            if (binding.createNameTxt.text?.isNotEmpty() == true){ //Check that name fiel is not empty
                if (sets > 0) { //Check that sets is more than 0
                    //Set values to Workout Obj
                    workout.name = binding.createNameTxt.text.toString()
                    workout.sets = sets
                    workout.reps = binding.createWorking.isChecked //Check if the switch is time or Reps

                    val bundle = Bundle()
                    bundle.putSerializable("workout", workout) // Save workout on bundle to pass it to next fragment
                    view.findNavController().navigate(R.id.createFragment2, bundle) //Launch next Fragment with info
                } else{
                    Toast.makeText(this.context, getString(R.string.setsCheckErrorToast), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this.context, getString(R.string.nameCheckErrorToast), Toast.LENGTH_SHORT).show()
            }

        }
        // Inflate the layout for this fragment
        return view
    }

}


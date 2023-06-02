package com.faesfa.tiwo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.faesfa.tiwo.databinding.FragmentCreate3Binding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*

class CreateFragment3 : Fragment() {
    //Initialize everything to use it all over class
    private var _binding: FragmentCreate3Binding? = null
    private val binding get() = _binding!!

    private lateinit var chestCategory : CircleImageView
    private lateinit var backCategory : CircleImageView
    private lateinit var shoulderCategory : CircleImageView
    private lateinit var armsCategory : CircleImageView
    private lateinit var legsCategory : CircleImageView
    private lateinit var absCategory : CircleImageView
    private lateinit var imagesList: ArrayList<CircleImageView>
    private lateinit var dataManager : DataManager

    private lateinit var workout : WorkoutsModelClass
    private var image = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = this.arguments
        dataManager = DataManager()
        workout = args?.getSerializable("workout") as WorkoutsModelClass
        val selectedColor = resources.getColor(R.color.AppColor)

        // Inflate the layout for this fragment
        _binding = FragmentCreate3Binding.inflate(inflater,container,false)
        val view = binding.root

        //Declare each category images and save them in list
        chestCategory = view.findViewById(R.id.category1)
        backCategory = view.findViewById(R.id.category2)
        shoulderCategory = view.findViewById(R.id.category3)
        armsCategory = view.findViewById(R.id.category4)
        legsCategory = view.findViewById(R.id.category5)
        absCategory = view.findViewById(R.id.category6)
        imagesList = ArrayList() //List to save images to
        imagesList.add(chestCategory)
        imagesList.add(backCategory)
        imagesList.add(shoulderCategory)
        imagesList.add(armsCategory)
        imagesList.add(legsCategory)
        imagesList.add(absCategory)

        //Check if working with Reps or Time
        if (workout.reps) { //working With Reps -> Set visibility of Time fields to gone
            binding.workLastTitle.visibility = View.GONE
            binding.workLastCreate.visibility = View.GONE
            binding.repsLastCreate.visibility = View.VISIBLE
            binding.intervalLastTitle.visibility = View.VISIBLE
            binding.repsLastCreate.visibility = View.VISIBLE
            binding.intervalLastCreate.visibility = View.VISIBLE
        } else { //working With Time -> Set visibility of Reps fields to gone
            binding.repsLastTitle.visibility = View.GONE
            binding.intervalLastTitle.visibility = View.GONE
            binding.repsLastCreate.visibility = View.GONE
            binding.intervalLastCreate.visibility = View.GONE
            binding.workLastTitle.visibility = View.VISIBLE
            binding.workLastCreate.visibility = View.VISIBLE
        }

        //Assign text to each field
        binding.nameLastCreate.text = workout.name
        binding.setsLastCreate.text = workout.sets.toString()
        binding.workingLastCreate.text = if (workout.reps) {getString(R.string.infoWorkingWithReps)} else {getString(R.string.infoWorkingWithTime)}
        binding.repsLastCreate.text = workout.num_reps.toString()
        binding.intervalLastCreate.text = workout.reps_time.toString()
        binding.workLastCreate.text = dataManager.convertTime(workout.work_time)
        binding.restLastCreate.text = dataManager.convertTime(workout.rest_time)
        
        //Set Click listener to each category image
        for (i in imagesList){
            i.setOnClickListener{
                for (j in imagesList){
                    j.circleBackgroundColor = resources.getColor(R.color.transparent)
                }
                when (it.id) {
                    chestCategory.id -> {
                        image = "Chest"
                        chestCategory.circleBackgroundColor = selectedColor }
                    backCategory.id -> {
                        image = "Back"
                        backCategory.circleBackgroundColor = selectedColor}
                    shoulderCategory.id -> {
                        image = "Shoulder"
                        shoulderCategory.circleBackgroundColor = selectedColor}
                    armsCategory.id -> {
                        image = "Arms"
                        armsCategory.circleBackgroundColor = selectedColor}
                    legsCategory.id -> {
                        image = "Legs"
                        legsCategory.circleBackgroundColor = selectedColor}
                    absCategory.id -> {
                        image = "Abs"
                        absCategory.circleBackgroundColor = selectedColor}
                    else -> {
                        Toast.makeText(this.context, "Pick a category", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.saveNewBtn.setOnClickListener {
            /**Save Workout Data In Json file**/
            if (checkIfImageSelected()){
                saveWorkout()
                val launchHome = Intent(this.context, MainActivity::class.java)
                startActivity(launchHome)
            } else {
                Toast.makeText(this.context, "Pick a category", Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveStartNewBtn.setOnClickListener {
            /**Save Workout Data In Json file
             * And Start Timer Activity passing the workout**/
            if (checkIfImageSelected()){
                saveWorkout()
                val launchTimer = Intent(this.context, TimerActivity::class.java)
                launchTimer.putExtra("selected_workout" , workout as Serializable)
                startActivity(launchTimer)
            } else {
                Toast.makeText(this.context, "Pick a category", Toast.LENGTH_SHORT).show()
            }
        }
        return view //Return the Inflated view
    }

    private fun checkIfImageSelected(): Boolean{
        return image != ""
    }

    private fun saveWorkout(){
        workout.category = image
        val jsonString = dataManager.getJsonFromFile(this.context)!! //Get String from File
        val workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        workouts.workouts.add(workout) //Add Workout to save
        val jsonCute = GsonBuilder().setPrettyPrinting().create() //Set Gson Look to pretty
        val newJson = jsonCute.toJson(workouts) //Turn the workouts Obj to String Again with Gson
        dataManager.saveJsonToFile(this.context, newJson) //Save new Json to File
    }
}
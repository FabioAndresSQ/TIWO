package com.faesfa.tiwo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

class CreateFragment3 : Fragment() {
    //Initialize everything to use it all over class
    private lateinit var nameLastCreate : TextView
    private lateinit var setsLastCreate : TextView
    private lateinit var workingLastCreate : TextView
    private lateinit var repsTitleLast : TextView
    private lateinit var repsLastCreate : TextView
    private lateinit var intervalTitleLast : TextView
    private lateinit var intervalLastCreate : TextView
    private lateinit var workTitleLast : TextView
    private lateinit var workLastCreate : TextView
    private lateinit var restLastCreate : TextView
    private lateinit var chestCategory : ImageView
    private lateinit var backCategory : ImageView
    private lateinit var shoulderCategory : ImageView
    private lateinit var armsCategory : ImageView
    private lateinit var legsCategory : ImageView
    private lateinit var calvesCategory : ImageView
    private lateinit var imagesList: ArrayList<ImageView>
    private lateinit var saveNewBtn: Button
    private lateinit var saveStartNewBtn: Button
    private lateinit var dataManager : DataManager

    private lateinit var workout : WorkoutsModelClass
    private var image = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create3, container, false)
        val args = this.arguments
        dataManager = DataManager()
        workout = args?.getSerializable("workout") as WorkoutsModelClass
        nameLastCreate = view.findViewById(R.id.nameLastCreate)
        setsLastCreate = view.findViewById(R.id.setsLastCreate)
        workingLastCreate = view.findViewById(R.id.workingLastCreate)
        repsTitleLast = view.findViewById(R.id.repsLastTitle)
        repsLastCreate = view.findViewById(R.id.repsLastCreate)
        intervalTitleLast = view .findViewById(R.id.intervalLastTitle)
        intervalLastCreate = view.findViewById(R.id.intervalLastCreate)
        workTitleLast = view.findViewById(R.id.workLastTitle)
        workLastCreate = view.findViewById(R.id.workLastCreate)
        restLastCreate = view.findViewById(R.id.restLastCreate)
        saveNewBtn = view.findViewById(R.id.saveNewBtn)
        saveStartNewBtn = view.findViewById(R.id.saveStartNewBtn)

        //Declare each category images and save them in list
        chestCategory = view.findViewById(R.id.category1)
        backCategory = view.findViewById(R.id.category2)
        shoulderCategory = view.findViewById(R.id.category3)
        armsCategory = view.findViewById(R.id.category4)
        legsCategory = view.findViewById(R.id.category5)
        calvesCategory = view.findViewById(R.id.category6)
        imagesList = ArrayList() //List to save images to
        imagesList.add(chestCategory)
        imagesList.add(backCategory)
        imagesList.add(shoulderCategory)
        imagesList.add(armsCategory)
        imagesList.add(legsCategory)
        imagesList.add(calvesCategory)

        //Check if working with Reps or Time
        if (workout.reps) { //working With Reps -> Set visibility of Time fields to gone
            workTitleLast.visibility = View.GONE
            workLastCreate.visibility = View.GONE
            repsTitleLast.visibility = View.VISIBLE
            intervalTitleLast.visibility = View.VISIBLE
            repsLastCreate.visibility = View.VISIBLE
            intervalLastCreate.visibility = View.VISIBLE
        } else { //working With Time -> Set visibility of Reps fields to gone
            repsTitleLast.visibility = View.GONE
            intervalTitleLast.visibility = View.GONE
            repsLastCreate.visibility = View.GONE
            intervalLastCreate.visibility = View.GONE
            workTitleLast.visibility = View.VISIBLE
            workLastCreate.visibility = View.VISIBLE
        }

        //Assign text to each field
        nameLastCreate.text = workout.name
        setsLastCreate.text = workout.sets.toString()
        workingLastCreate.text = if (workout.reps) {"Repetitions"} else {"Time"}
        repsLastCreate.text = workout.num_reps.toString()
        intervalLastCreate.text = workout.reps_time.toString()
        workLastCreate.text = dataManager.convertTime(workout.work_time)
        restLastCreate.text = dataManager.convertTime(workout.rest_time)
        
        //Set Click listener to each category image
        for (i in imagesList){
            i.setOnClickListener{
                for (j in imagesList){
                    j.setBackgroundColor(resources.getColor(R.color.transparent))
                }
                it.setBackgroundColor(resources.getColor(R.color.AppBlue))
                when (it.id) {
                    chestCategory.id -> {
                        Toast.makeText(this.context, "Chest Category", Toast.LENGTH_SHORT).show()}
                    backCategory.id -> {
                        Toast.makeText(this.context, "Back Category", Toast.LENGTH_SHORT).show()}
                    shoulderCategory.id -> {
                        Toast.makeText(this.context, "Shoulder Category", Toast.LENGTH_SHORT).show()}
                    armsCategory.id -> {
                        Toast.makeText(this.context, "Arms Category", Toast.LENGTH_SHORT).show()}
                    legsCategory.id -> {
                        Toast.makeText(this.context, "Legs Category", Toast.LENGTH_SHORT).show()}
                    calvesCategory.id -> {
                        Toast.makeText(this.context, "Calf Category", Toast.LENGTH_SHORT).show()}
                    else -> {
                        Toast.makeText(this.context, "Pick An Image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        saveNewBtn.setOnClickListener {
            /**Save Workout Data In Json file**/
            saveWorkout()
            val launchHome = Intent(this.context, MainActivity::class.java)
            startActivity(launchHome)
        }

        saveStartNewBtn.setOnClickListener {
            /**Save Workout Data In Json file
             * And Start Timer Activity passing the workout**/
            saveWorkout()
            val launchTimer = Intent(this.context, TimerActivity::class.java)
            launchTimer.putExtra("selected_workout" , workout as Serializable)
            startActivity(launchTimer)
        }
        return view //Return the Inflated view
    }

    private fun saveWorkout(){
        val jsonString = dataManager.getJsonFromFile(this.context)!! //Get String from File
        val workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        workouts.workouts.add(workout) //Add Workout to save
        val jsonCute = GsonBuilder().setPrettyPrinting().create() //Set Gson Look to pretty
        val newJson = jsonCute.toJson(workouts) //Turn the workouts Obj to String Again with Gson
        dataManager.saveJsonToFile(this.context, newJson) //Save new Json to File
    }
}
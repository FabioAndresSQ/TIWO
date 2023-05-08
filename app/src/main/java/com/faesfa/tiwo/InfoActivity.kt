package com.faesfa.tiwo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView
import java.io.*

class InfoActivity : AppCompatActivity() {
    //Initialize everything
    private lateinit var workouts: Workouts
    private lateinit var dataManager: DataManager
    private lateinit var infoImage:CircleImageView
    private lateinit var infoNameTxt:TextView
    private lateinit var infoSetsTxt:TextView
    private lateinit var infoRepsTitle:TextView
    private lateinit var infoRepsTxt:TextView
    private lateinit var infoWorkTitle:TextView
    private lateinit var infoWorkTxt:TextView
    private lateinit var infoRestTxt:TextView
    private lateinit var infoStartBtn:LinearLayout
    private lateinit var infoDeleteBtn:LinearLayout
    private lateinit var toolBar : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        dataManager = DataManager()
        val workout : WorkoutsModelClass = intent?.getSerializableExtra("selected_workout") as WorkoutsModelClass //Save workout got from Prev Activity
        val position = intent?.getSerializableExtra("position") as Int //Save position got from Prev Activity
        //Declare all views
        infoImage = findViewById(R.id.infoImg)
        infoNameTxt = findViewById(R.id.infoNameTxt)
        infoSetsTxt = findViewById(R.id.infoSetsTxt)
        infoRepsTitle = findViewById(R.id.infoReps)
        infoRepsTxt = findViewById(R.id.infoRepsTxt)
        infoWorkTitle = findViewById(R.id.infoWorkTitle)
        infoWorkTxt = findViewById(R.id.infoWorkTxt)
        infoRestTxt = findViewById(R.id.infoRestTxt)
        infoStartBtn = findViewById(R.id.infoStartBtn)
        infoDeleteBtn = findViewById(R.id.infoDeleteBtn)


        when (workout.category){
            "Chest" -> {infoImage.setImageResource(R.drawable.chest_ic)}
            "Back" -> {infoImage.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {infoImage.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {infoImage.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {infoImage.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {infoImage.setImageResource(R.drawable.abs_ic)}
        }
        @RequiresApi(Build.VERSION_CODES.M)
        infoImage.borderColor = this.getColor(R.color.AppColor)
        infoImage.borderWidth = 5

        infoNameTxt.text = workout.name.uppercase()
        infoSetsTxt.text = workout.sets.toString()
        if (workout.reps){ //Working with reps, set visibility
            infoRepsTitle.visibility = View.VISIBLE
            infoRepsTxt.visibility = View.VISIBLE
            infoRepsTxt.text = workout.num_reps.toString()
            infoWorkTitle.text = this.getString(R.string.infoNumRepsIntervalTitle) //Set title to working with Reps
            val duration = "${workout.reps_time} ${getString(R.string.infoIntervalSecs)}"
            infoWorkTxt.text = duration
        }else{ //Working with Time, set visibility
            infoRepsTitle.visibility = View.GONE
            infoRepsTxt.visibility = View.GONE
            infoWorkTitle.text = this.getString(R.string.infoWorkTimeTitle) //Set title to working with time
            val duration = dataManager.convertTime(workout.work_time)
            infoWorkTxt.text = duration
        }
        val restDur = dataManager.convertTime(workout.rest_time)
        infoRestTxt.text = restDur

        //Set listeners for Start and delete Btn
        infoStartBtn.setOnClickListener {
            val launchTimer = Intent(this, TimerActivity::class.java)
            launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
            startActivity(launchTimer)
        }

        infoDeleteBtn.setOnClickListener {
            try {
                val jsonString = dataManager.getJsonFromFile(this)!! //Get String from File
                workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn Json String into Workouts obj
                workouts.workouts.removeAt(position) //Remove workout object on specified position from workouts
                val jsonCute = GsonBuilder().setPrettyPrinting().create()
                val newJson = jsonCute.toJson(workouts) //Create the new Json string with Gson
                dataManager.saveJsonToFile(this, newJson) //Save Json to file
                Toast.makeText(this, "Workout Deleted", Toast.LENGTH_SHORT).show()
                /*TODO something before Starting Home screen*/
                val launchHome = Intent(this, MainActivity::class.java)
                startActivity(launchHome)
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(applicationContext, "Error to Delete Workout", Toast.LENGTH_SHORT).show()
            }
        }

    }

}




package com.faesfa.tiwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.io.*

class MainActivity : AppCompatActivity(), WorkoutsAdapter.OnItemClickListener {
    //Initialize everything
    private var backPressedOnce = false
    private lateinit var plusBtn : LinearLayout
    private lateinit var quickBtn : LinearLayout
    private lateinit var createBtn : LinearLayout
    private lateinit var quickTxtHome: TextView
    private lateinit var createTxtHome: TextView
    private lateinit var workouts : Workouts
    private lateinit var dataManager: DataManager
    private lateinit var jsonString : String
    private var showingOpts = true
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition{ false }

        toolBar = findViewById(R.id.includeAppBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        dataManager = DataManager()
        plusBtn = findViewById(R.id.mainBtn)
        quickBtn = findViewById(R.id.quickBtn)
        createBtn = findViewById(R.id.createBtn)
        checkIfBtnAreShowing()

        plusBtn.setOnClickListener {
            checkIfBtnAreShowing()
        }

        quickBtn.setOnClickListener {
            val launchQuick = Intent(this, QuickActivity::class.java)
            startActivity(launchQuick)
        }

        createBtn.setOnClickListener{
            val launchCreate = Intent(this, CreateActivity::class.java)
            startActivity(launchCreate)
        }
        //Get data from Json file
        try {
            jsonString = dataManager.getJsonFromFile(this)!!
        } catch (e : Exception){ //if File is Empty or null Create it
            val createJsonString = "{\"workouts\": []}"
            dataManager.saveJsonToFile(this, createJsonString)
            jsonString = dataManager.getJsonFromFile(this)!!
        }

        workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson


        val mRecyclerView = findViewById<RecyclerView>(R.id.rvWorkouts)
        mRecyclerView.layoutManager = LinearLayoutManager(this)//Set RecyclerView to linearLayout
        val itemAdapter = WorkoutsAdapter(this, workouts.workouts, this)//Set ItemAdapter to Workouts workout from Gson created obj
        mRecyclerView.adapter = itemAdapter //Set the RecyclerView adapter to itemAdapter
    }

    override fun onItemClick(item: WorkoutsModelClass, adapterPosition : Int) { //Check on item clicked passing item and item position
        val launchDetails = Intent(this, InfoActivity::class.java)
        launchDetails.putExtra("selected_workout" , item as Serializable) //Save item on Intend
        launchDetails.putExtra("position" , adapterPosition as Serializable) //Save item position on Intend
        startActivity(launchDetails)
    }

    override fun onBackPressed() { //Control the back button pressed
        if (backPressedOnce) {
            finishAffinity() //Close App
        }
        this.backPressedOnce = true
        Toast.makeText(this, getString(R.string.backToExitApp), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ backPressedOnce=false },2000)
    }

    private fun checkIfBtnAreShowing(){
        if (!showingOpts) {
            quickBtn.visibility = View.VISIBLE
            createBtn.visibility = View.VISIBLE
            plusBtn.rotation = 45F
            showingOpts = true
        } else{
            quickBtn.visibility = View.GONE
            createBtn.visibility = View.GONE
            plusBtn.rotation = 0F
            showingOpts = false
        }
    }

}
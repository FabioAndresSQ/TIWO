package com.faesfa.tiwo

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.*

class MainActivity : AppCompatActivity(), WorkoutsAdapter.OnItemClickListener {
    //Initialize everything
    private var backPressedOnce = false
    private lateinit var plusBtn : ImageView
    private lateinit var quickBtn : LinearLayout
    private lateinit var createBtn : LinearLayout
    private lateinit var workouts : Workouts
    private lateinit var dataManager: DataManager
    private lateinit var jsonString : String
    private lateinit var emptyLayoutInfo : ConstraintLayout
    private var showingOpts = true
    private lateinit var toolBar : Toolbar
    private lateinit var createTxtView : TextView
    private lateinit var quickTxtView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition{ false }

        toolBar = findViewById(R.id.includeAppBar)
        emptyLayoutInfo = findViewById(R.id.emptyLayoutInfo)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        dataManager = DataManager()
        plusBtn = findViewById(R.id.mainBtn)
        quickBtn = findViewById(R.id.quickBtn)
        createBtn = findViewById(R.id.createBtn)
        emptyLayoutInfo = findViewById(R.id.emptyLayoutInfo)
        createTxtView = findViewById(R.id.createTxtView)
        quickTxtView = findViewById(R.id.quickTxtView)
        checkIfBtnAreShowing()

        quickBtn.visibility = View.GONE
        createBtn.visibility = View.GONE

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
        jsonString = try {
            dataManager.getJsonFromFile(this)!!
        } catch (e : Exception){ //if File is Empty or null Create it
            val createJsonString = "{\"workouts\": []}"
            dataManager.saveJsonToFile(this, createJsonString)
            dataManager.getJsonFromFile(this)!!
        }

        workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        Log.d("START", workouts.workouts.toString())
        if (workouts.workouts.isEmpty()){
            emptyLayoutInfo.visibility = View.VISIBLE
        } else{
            emptyLayoutInfo.visibility = View.GONE
        }
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
            ObjectAnimator.ofFloat(plusBtn, "rotation", 45f*5).apply {
                duration = 500
                start()
            }
            val animateBtns = ObjectAnimator.ofFloat(quickBtn, "translationY", -320f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(createBtn, "translationY", -160f).apply {
                duration = 300
                start()
            }
            animateBtns.doOnEnd {
                quickTxtView.visibility = View.VISIBLE
                createTxtView.visibility = View.VISIBLE
            }
        } else{
            quickTxtView.visibility = View.GONE
            createTxtView.visibility = View.GONE
            ObjectAnimator.ofFloat(plusBtn, "rotation", 0f).apply {
                duration = 500
                start()
            }
            val animateBtns = ObjectAnimator.ofFloat(quickBtn, "translationY", 0f).apply {
                duration = 300
                start()
            }
            ObjectAnimator.ofFloat(createBtn, "translationY", 0f).apply {
                duration = 300
                start()
            }
            animateBtns.doOnEnd {
                quickBtn.visibility = View.GONE
                createBtn.visibility = View.GONE
            }
            showingOpts = false
        }
    }

}
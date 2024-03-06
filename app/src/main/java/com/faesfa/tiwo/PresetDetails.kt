package com.faesfa.tiwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.faesfa.tiwo.databinding.ActivityPresetDetailsBinding
import com.faesfa.tiwo.domain.model.Preset
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class PresetDetails : AppCompatActivity() {

    @Inject lateinit var dataManager : DataManager

    private lateinit var binding : ActivityPresetDetailsBinding
    private lateinit var preset : Preset

    //Default values
    private var sets = 3
    private var reps = 10
    private var interval = 1.5
    private var workMinutes = 0
    private var workSeconds = 0
    private var restMinutes = 0
    private var restSeconds = 0
    private var category = ""
    private lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.includePresetsBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)

        //Receive preset selected
        preset = intent.getSerializableExtra("selected_preset") as Preset

        startBannerAds()

        //Set Info to display
        binding.presetNameTxt.text = preset.name?.capitalize()
        setPresetImage(preset.gifUrl)
        
        setPresetCategory(preset.bodyPart)


        //Set Visibility by default
        changeVisibility(binding.presetWorkingMode.isChecked)
        binding.presetWorkingMode.setOnCheckedChangeListener { _, isChecked ->
            changeVisibility(isChecked)
        }

        //Set listener to sets add or remove
        binding.presetSetsTxt.text = sets.toString()
        binding.presetAddSetsBtn.setOnClickListener {
            if (sets > 0) {
                sets++
                binding.presetSetsTxt.text = sets.toString()
                binding.presetRemoveSetsBtn.visibility = View.VISIBLE
            }
        }
        binding.presetRemoveSetsBtn.setOnClickListener {
            if (sets > 1){
                sets--
                binding.presetSetsTxt.text = sets.toString()
            } else if (sets == 1) {
                binding.presetRemoveSetsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Reps
        binding.presetAddRepsBtn.setOnClickListener {
            if (reps > 0) {
                reps++
                binding.presetRepsTxt.text = reps.toString()
                binding.presetRemoveRepsBtn.visibility = View.VISIBLE
            }
        }
        binding.presetRemoveRepsBtn.setOnClickListener {
            if (reps > 1){
                reps--
                binding.presetRepsTxt.text = reps.toString()
            } else if (reps == 1) {
                binding.presetRemoveRepsBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for add or remove Interval
        binding.presetAddIntervalBtn.setOnClickListener {
            if (interval > 0.0) {
                interval += 0.5
                binding.presetIntervalTxt.text = interval.toString()
                binding.presetRemoveIntervalBtn.visibility = View.VISIBLE
            }
        }
        binding.presetRemoveIntervalBtn.setOnClickListener {
            if (interval > 0.6){
                interval -= 0.5
                binding.presetIntervalTxt.text = interval.toString()
            } else if (interval == 0.5) {
                binding.presetRemoveIntervalBtn.visibility = View.INVISIBLE
            }
        }

        //Listeners for the NumberPickers on Work Time
        binding.workMinutesPreset.maxValue = 59
        binding.workMinutesPreset.minValue = 0
        binding.workSecondsPreset.maxValue = 59
        binding.workSecondsPreset.minValue = 0
        binding.workMinutesPreset.setOnValueChangedListener { _, _, newVal ->
            workMinutes = newVal
        }
        binding.workSecondsPreset.setOnValueChangedListener { _, _, newVal ->
            workSeconds = newVal
        }

        //Listener for the NumberPickers on Rest Time
        binding.restMinutesPreset.maxValue = 59
        binding.restMinutesPreset.minValue = 0
        binding.restSecondsPreset.maxValue = 59
        binding.restSecondsPreset.minValue = 0
        binding.restMinutesPreset.setOnValueChangedListener { _, _, newVal ->
            restMinutes = newVal
        }
        binding.restSecondsPreset.setOnValueChangedListener { _, _, newVal ->
            restSeconds = newVal
        }

        //Listener for save and start btn
        binding.saveNewPresetBtn.setOnClickListener {
            savePresetAsWorkout(it)
        }

        binding.saveStartNewPresetBtn.setOnClickListener {
            savePresetAsWorkout(it)
        }

        binding.scrollViewLayout.smoothScrollTo(0, 100)

        /*val animateScroll = ObjectAnimator.ofFloat(binding.scrollViewLayout, "translationY", -300f).apply {
            duration = 500
            start()
        }
        animateScroll.doOnEnd {
            ObjectAnimator.ofFloat(binding.scrollViewLayout, "translationY", 0f).apply {
                duration = 500
                start()
            }
        }*/

    }

    private fun savePresetAsWorkout(btn : View) {
        val workout : WorkoutsModelClass
        val workTime = (workMinutes * 60) + workSeconds
        val restTime = (restMinutes * 60) + restSeconds
        if (binding.presetWorkingMode.isChecked){ //Checks if Working with reps
            if (reps < 1){ //Checks for Number of reps to not be 0
                Toast.makeText(this, getString(R.string.repsCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            if (interval < 1){ //Checks for reps interval to not be 0
                Toast.makeText(this, getString(R.string.intervalCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            if (restTime < 1) { //Checks that Rest is not 0
                Toast.makeText(this, getString(R.string.restTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            if (sets < 1) { //Checks that Sets is not 0
                Toast.makeText(this, getString(R.string.setsCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            //Assign values to workout Obj if statements are passed
            workout  = WorkoutsModelClass(
                preset.id,
                preset.name,
                sets,
                true,
                reps,
                interval,
                0,
                restTime,
                category)

        } else { //Working with Time
            if (workTime < 1) { //Checks for work to not be 0
                Toast.makeText(this, getString(R.string.workTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            if (restTime < 1) { //Checks for rest to not be 0
                Toast.makeText(this, getString(R.string.restTimeCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            if (sets < 1) {
                Toast.makeText(this, getString(R.string.setsCheckErrorToast), Toast.LENGTH_SHORT).show()
                return
            }
            //Assign values to workout Obj if statements are passed
            workout  = WorkoutsModelClass(
                preset.id,
                preset.name,
                sets,
                false,
                0,
                0.0,
                workTime,
                restTime,
                category)
        }

        //Save and Launch Timer or Home Activity
        saveWorkout(workout)
        if (btn.id == binding.saveStartNewPresetBtn.id){
            val launchTimer = Intent(this, TimerActivity::class.java)
            launchTimer.putExtra("selected_workout" , workout as Serializable) //Add workout Obj to pass it to timer
            startActivity(launchTimer)
        } else {
            val launchHome = Intent(this, MainActivity::class.java)
            startActivity(launchHome)
        }

    }

    private fun setPresetImage(gifUrl: String?) {
        binding.loadingPresetImage.visibility = View.VISIBLE
        Glide.with(this).asGif()
            .load(gifUrl)
            .listener(object : RequestListener<GifDrawable?> {

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.loadingPresetImage.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.loadingPresetImage.visibility = View.VISIBLE
                    return false
                }

            }).into(binding.presetImg)

    }

    private fun setPresetCategory(bodyPart: String?) {
        binding.presetCategory.borderColor = resources.getColor(R.color.AppColor)
        binding.presetCategory.borderWidth = 5
        when(bodyPart) {
            "chest" -> {
                binding.presetCategory.setImageResource(R.drawable.chest_ic)
                binding.presetCategoryTxt.text = getString(R.string.chestCategoryTxt)
                category = "Chest"
            }

            "back" -> {
                binding.presetCategory.setImageResource(R.drawable.back_ic)
                binding.presetCategoryTxt.text = getString(R.string.backCategoryTxt)
                category = "Back"
            }

            "shoulders" -> {
                binding.presetCategory.setImageResource(R.drawable.shoulder_ic)
                binding.presetCategoryTxt.text = getString(R.string.shoulderCategoryTxt)
                category = "Shoulder"
            }

            "neck" -> {
                binding.presetCategory.setImageResource(R.drawable.shoulder_ic)
                binding.presetCategoryTxt.text = getString(R.string.shoulderCategoryTxt)
                category = "Shoulder"
            }

            "waist" -> {
                binding.presetCategory.setImageResource(R.drawable.abs_ic)
                binding.presetCategoryTxt.text = getString(R.string.absCategoryTxt)
                category = "Abs"
            }

            "cardio" -> {
                binding.presetCategory.setImageResource(R.drawable.abs_ic)
                binding.presetCategoryTxt.text = getString(R.string.absCategoryTxt)
                category = "Abs"
            }

            "lower arms" -> {
                binding.presetCategory.setImageResource(R.drawable.arm_ic)
                binding.presetCategoryTxt.text = getString(R.string.armsCategoryTxt)
                category = "Arms"
            }

            "upper arms" -> {
                binding.presetCategory.setImageResource(R.drawable.arm_ic)
                binding.presetCategoryTxt.text = getString(R.string.armsCategoryTxt)
                category = "Arms"
            }

            "lower legs" -> {
                binding.presetCategory.setImageResource(R.drawable.legs_ic)
                binding.presetCategoryTxt.text = getString(R.string.legsCategoryTxt)
                category = "Legs"
            }

            "upper legs" -> {
                binding.presetCategory.setImageResource(R.drawable.legs_ic)
                binding.presetCategoryTxt.text = getString(R.string.legsCategoryTxt)
                category = "Legs"
            }
        }
    }

    private fun changeVisibility(checked: Boolean) {
        if (checked){
            binding.presetWorkTimeLayout.visibility = View.GONE
            binding.presetRepsLayout.visibility = View.VISIBLE
        } else {
            binding.presetWorkTimeLayout.visibility = View.VISIBLE
            binding.presetRepsLayout.visibility = View.GONE
        }
    }

    private fun saveWorkout(workout: WorkoutsModelClass){
        val jsonString = dataManager.getJsonFromFile(this)!! //Get String from File
        val workouts = Gson().fromJson(jsonString, Workouts::class.java) //Turn String into Model Obj with Gson
        workouts.workouts.add(workout) //Add Workout to save
        val jsonCute = GsonBuilder().setPrettyPrinting().create() //Set Gson Look to pretty
        val newJson = jsonCute.toJson(workouts) //Turn the workouts Obj to String Again with Gson
        dataManager.saveJsonToFile(this, newJson) //Save new Json to File
    }

    private fun startBannerAds(){
        //INITIALIZING BANNER ADS AND REQUESTING IT
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adViewPreset.loadAd(adRequest)

        binding.adViewPreset.adListener = object : AdListener(){
            override fun onAdClicked() {
                Log.d("AD_BANNER", "AD LOADED CLICKED")
                super.onAdClicked()
            }

            override fun onAdClosed() {
                Log.d("AD_BANNER", "AD LOADED CLOSED")
                super.onAdClosed()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.e("AD_BANNER", p0.toString())
                super.onAdFailedToLoad(p0)
            }

            override fun onAdImpression() {
                Log.d("AD_BANNER", "AD IMPRESSION COUNTED")
                super.onAdImpression()
            }

            override fun onAdLoaded() {
                Log.d("AD_BANNER", "AD LOADED SUCCESSFUL")
                super.onAdLoaded()
            }

            override fun onAdOpened() {
                Log.d("AD_BANNER", "AD LOADED OPENED OVERLAY")
                super.onAdOpened()
            }
        }
    }
}
package com.faesfa.tiwo

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor(){

    fun getJsonFromFile(context: Context?): String?{
        val json: String?
        try {
            val bufferedReader: BufferedReader = File(context?.filesDir , "workouts.json").bufferedReader()
            val readString = bufferedReader.use { it.readText() }
            json = readString
            println(json)
        }catch (ex: IOException){
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun saveJsonToFile(context: Context?, json: String){
        try {
            val writer = FileWriter( File(context?.filesDir , "workouts.json"))
            writer.use { it.write(json) }
        }catch (ex: IOException){
            ex.printStackTrace()
        } finally {
            Toast.makeText(context, "Saved Correctly", Toast.LENGTH_SHORT).show()
        }
    }

    fun savePresetsJsonToFile(context: Context?, json: String){
        try {
            val writer = FileWriter( File(context?.filesDir , "workoutsPresets.json"))
            writer.use { it.write(json) }
        }catch (ex: IOException){
            ex.printStackTrace()
        } finally {
            Toast.makeText(context, "Saved Correctly", Toast.LENGTH_SHORT).show()
        }
    }

    fun convertTime(secs: Int): String {
        val minutes = secs / 60
        val seconds = secs % 60
        val minutesTxt : String = if (minutes < 10){
            "0$minutes"
        } else {
            "$minutes"
        }
        val secondsTxt : String = if (seconds < 10){
            "0$seconds"
        } else {
            "$seconds"
        }
        return "$minutesTxt:$secondsTxt"
    }

    fun convertTimeTimer(secs: Int): Array<String> {
        val minutes = secs / 60
        val seconds = secs % 60
        val minutesTxt: String = if (minutes < 10){
            "0$minutes"
        } else {
            "$minutes"
        }
        val secondsTxt: String = if (seconds < 10){
            "0$seconds"
        } else {
            "$seconds"
        }
        return arrayOf(minutesTxt, secondsTxt)
    }

}
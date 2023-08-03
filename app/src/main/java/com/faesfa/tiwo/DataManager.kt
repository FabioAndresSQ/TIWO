package com.faesfa.tiwo

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.faesfa.tiwo.R
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.faesfa.tiwo.domain.GetAllPresetsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor(){

    @Inject lateinit var getAllPresetsUseCase: GetAllPresetsUseCase
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
            //Toast.makeText(context, Resources.getSystem().getString(R.string.savedConfirmToast), Toast.LENGTH_SHORT).show()
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

    fun checkDbDate(activity: FragmentActivity?, errorLoading: Boolean) {
        Log.d("DATEMATH", "Starting Date Check")

        val sharedPref = activity?.getSharedPreferences(activity.resources.getString(R.string.last_db_saved), Context.MODE_PRIVATE)
        val currentDate = Date().time
        val savedDate = sharedPref?.getLong("db_date", 0)
        Log.d("DATEMATH", "SharedPref = $savedDate")
        if (errorLoading){
            if (hasInternet(activity!!) == true) {
                getAllPresetsFromApi(activity)
                //Update Database date
                val prefs = activity?.getSharedPreferences(activity.resources.getString(R.string.last_db_saved), Context.MODE_PRIVATE)?.edit()
                prefs?.putLong("db_date", currentDate)
                prefs?.apply()
            } else {
                Toast.makeText(activity, activity.resources.getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (savedDate != null) {
            if (savedDate > 0) {
                // Verify if current date is 12 hours later
                val diff: Long = currentDate - savedDate
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                Log.d("DATEMATH", "Database date found = $savedDate")
                Log.d("DATEMATH", "current date: $currentDate")
                Log.d("DATEMATH", "Difference: days=$days, hours=$hours, minutes=$minutes, seconds=$seconds")
                if (hours > 12 || days > 0){
                    //Update DataBase from Api
                    if (hasInternet(activity) == true) {
                        getAllPresetsFromApi(activity)
                        //Update Database date
                        val prefs = activity?.getSharedPreferences(activity.resources.getString(R.string.last_db_saved), Context.MODE_PRIVATE)?.edit()
                        prefs?.putLong("db_date", currentDate)
                        prefs?.apply()
                    } else {
                        Toast.makeText(activity, activity.resources.getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                    }

                }
            } else {
                // DataBase is not created so call for api and save current date
                Log.d("DATEMATH", "Database Not Created: $currentDate")
                if (hasInternet(activity) == true) {
                    getAllPresetsFromApi(activity)
                    //Update Database date
                    val prefs = activity?.getSharedPreferences(
                        activity.resources.getString(R.string.last_db_saved),
                        Context.MODE_PRIVATE
                    )?.edit()
                    prefs?.putLong("db_date", currentDate)
                    prefs?.apply()
                } else {
                    Toast.makeText(activity, activity.resources.getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hasInternet(context: FragmentActivity): Boolean? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @RequiresApi(Build.VERSION_CODES.M)
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun getAllPresetsFromApi(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val result = getAllPresetsUseCase(context)
            if (result.isEmpty()){
                run { CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, context.resources.getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                } }
            }
        }
    }

}
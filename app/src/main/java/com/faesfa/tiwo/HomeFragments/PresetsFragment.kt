package com.faesfa.tiwo.HomeFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.faesfa.tiwo.DataManager
import com.faesfa.tiwo.PresetDetails
import com.faesfa.tiwo.PresetsAdapter
import com.faesfa.tiwo.R
import com.faesfa.tiwo.data.PresetsRepository
import com.faesfa.tiwo.databinding.FragmentPresetsBinding
import com.faesfa.tiwo.domain.GetAllPresetsUseCase
import com.faesfa.tiwo.domain.model.Preset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class PresetsFragment : Fragment() , PresetsAdapter.OnPresetClickListener, OnQueryTextListener  {

    @Inject lateinit var dataManager : DataManager
    @Inject lateinit var repository : PresetsRepository
    @Inject lateinit var getAllPresetsUseCase: GetAllPresetsUseCase

    private var _binding : FragmentPresetsBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapter: PresetsAdapter
    private val presetsList = mutableListOf<Preset>()
    private val isLoading = MutableLiveData<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPresetsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.presetsEmptyLayout.visibility = View.VISIBLE

        checkDbDate()


        isLoading.observe(requireActivity()) {
            binding.loadingBar.isVisible = it
            binding.rvPresets.isVisible = !it
        }

        binding.searchPreset.setOnQueryTextListener(this)
        startRecyclerView()

        binding.chestPresets.setOnClickListener {
            getPresetsByMuscle("chest")
            assingButtonColor(it)
        }
        binding.backPresets.setOnClickListener {
            getPresetsByMuscle("back")
            assingButtonColor(it)
        }
        binding.absPresets.setOnClickListener {
            getPresetsByMuscle("waist")
            assingButtonColor(it)
        }
        binding.shoulderPresets.setOnClickListener {
            getPresetsByMuscle("shoulders")
            assingButtonColor(it)
        }
        binding.cardioPresets.setOnClickListener {
            getPresetsByMuscle("cardio")
            assingButtonColor(it)
        }
        binding.neckPresets.setOnClickListener {
            getPresetsByMuscle("neck")
            assingButtonColor(it)
        }
        binding.upperArmsPresets.setOnClickListener {
            getPresetsByMuscle("upper arms")
            assingButtonColor(it)
        }
        binding.lowerArmsPresets.setOnClickListener {
            getPresetsByMuscle("lower arms")
            assingButtonColor(it)
        }
        binding.upperLegsPresets.setOnClickListener {
            getPresetsByMuscle("upper legs")
            assingButtonColor(it)
        }
        binding.lowerLegsPresets.setOnClickListener {
            getPresetsByMuscle("lower legs")
            assingButtonColor(it)
        }

        return view
    }

    private fun assingButtonColor(button: View?){
        val btns = binding.scrollCategory.touchables
        for (i in btns){
            if (button != null && i.id == button.id){
                i.background = ResourcesCompat.getDrawable(resources, R.drawable.save_btn_shape, null)
            } else {
                i.background = ResourcesCompat.getDrawable(resources, R.drawable.preset_menu_btn_shape, null)
            }
        }
    }

    private fun checkDbDate() {
        Log.d("DATEMATH", "Starting Date Check")
        val sharedPref = activity?.getSharedPreferences(getString(R.string.last_db_saved), Context.MODE_PRIVATE)
        val currentDate = Date().time
        val savedDate = sharedPref?.getLong("db_date", 0)
        Log.d("DATEMATH", "SharedPref = $savedDate")
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
                    getAllPresetsFromApi()
                    //Update Database date
                    val prefs = activity?.getSharedPreferences(getString(R.string.last_db_saved), Context.MODE_PRIVATE)?.edit()
                    prefs?.putLong("db_date", currentDate)
                    prefs?.apply()
                }
            } else {
                // DataBase is not created so call for api and save current date
                Log.d("DATEMATH", "Database Not Created: $currentDate")
                val prefs = activity?.getSharedPreferences(getString(R.string.last_db_saved), Context.MODE_PRIVATE)?.edit()
                prefs?.putLong("db_date", currentDate)
                prefs?.apply()
                //Update DataBase from Api
                getAllPresetsFromApi()
            }
        }
    }

    private fun startRecyclerView() {
        adapter = PresetsAdapter(requireContext(), presetsList, this)
        binding.rvPresets.layoutManager = LinearLayoutManager(context)
        binding.rvPresets.adapter = adapter
    }

    private fun getAllPresetsFromApi(){
        CoroutineScope(Dispatchers.IO).launch {
            val result = getAllPresetsUseCase(requireContext())
            if (result.isEmpty()){
                run { CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                } }
            }
        }
    }
    private fun getPresetsByMuscle(muscle: String){
        binding.presetsEmptyLayout.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch{
            isLoading.postValue(true)
            val apiResponse = repository.getPresetsByMuscleFromDb(muscle)
            run { CoroutineScope(Dispatchers.Main).launch {
                if (apiResponse.isEmpty()){
                    //Error
                    binding.rvPresets.smoothScrollToPosition(0)
                    isLoading.postValue(false)
                    presetsList.clear()
                    binding.emptyResultImg.setImageResource(R.drawable.not_found)
                    binding.emptyResultTxt.text = getString(R.string.presetNotFoundTxt)
                    binding.presetsEmptyLayout.visibility = View.VISIBLE
                } else {
                    binding.rvPresets.smoothScrollToPosition(0)
                    presetsList.clear()
                    presetsList.addAll(apiResponse)
                    binding.presetsEmptyLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    isLoading.postValue(false)
                }

                /*TRANSLATE RESULT ON FUTURE UPDATE
                if (apiResponse.isNotEmpty()){
                    var stringToTranslate = "["
                    for (i in apiResponse){
                        stringToTranslate += "{\"val\":[\"${i.name.toString()}\",\n"
                        stringToTranslate += "\"${i.target.toString()}\",\n"
                        if (apiResponse.get(apiResponse.lastIndex) == i){
                            stringToTranslate += "\"${i.equipment.toString()}\"]}"
                        } else {
                            stringToTranslate += "\"${i.equipment.toString()}\"]},\n"
                        }
                    }
                    stringToTranslate += "]"
                    dataManager.savePresetsJsonToFile(requireContext(), stringToTranslate)
                    println(stringToTranslate)
                    Log.d("TRANSLATE", stringToTranslate)
                }*/
            } }

        }

    }

    private fun getPresetsBySearch(query: String){
        CoroutineScope(Dispatchers.IO).launch{
            isLoading.postValue(true)
            val apiResponse = repository.getPresetsBySearchFromDb(query.toLowerCase())

            run { CoroutineScope(Dispatchers.Main).launch {
                if (apiResponse.isEmpty()){
                    //Error
                    Log.d("Search result", "getPresetsBySearch: $apiResponse")
                    isLoading.postValue(false)
                    binding.emptyResultImg.setImageResource(R.drawable.not_found)
                    binding.emptyResultTxt.text = getString(R.string.presetNotFoundTxt)
                    binding.presetsEmptyLayout.visibility = View.VISIBLE
                    presetsList.clear()
                } else {
                    binding.rvPresets.smoothScrollToPosition(0)
                    presetsList.clear()
                    presetsList.addAll(apiResponse)
                    binding.presetsEmptyLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    isLoading.postValue(false)
                }
            } }

        }

    }

    override fun onItemClick(item: Preset, adapterPosition: Int) {
        val launchPresetDetails = Intent(this.context, PresetDetails::class.java)
        launchPresetDetails.putExtra("selected_preset" , item as Serializable) //Save item on Intend
        startActivity(launchPresetDetails)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            getPresetsBySearch(query)
            assingButtonColor(null)
        }
        binding.searchPreset.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
package com.faesfa.tiwo.HomeFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.faesfa.tiwo.DataManager
import com.faesfa.tiwo.InfoActivity
import com.faesfa.tiwo.PresetsAdapter
import com.faesfa.tiwo.data.model.PresetsModel
import com.faesfa.tiwo.R
import com.faesfa.tiwo.data.network.APIService
import com.faesfa.tiwo.core.RetrofitHelper
import com.faesfa.tiwo.data.PresetsRepository
import com.faesfa.tiwo.databinding.FragmentPresetsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.Serializable
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PresetsFragment : Fragment() , PresetsAdapter.OnPresetClickListener, OnQueryTextListener  {

    @Inject lateinit var dataManager : DataManager
    @Inject lateinit var repository : PresetsRepository

    private var _binding : FragmentPresetsBinding ?= null
    private val binding get() = _binding!!
    private lateinit var adapter: PresetsAdapter
    private val presetsList = mutableListOf<PresetsModel>()


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

        binding.searchPreset.setOnQueryTextListener(this)
        startRecyclerView()

        binding.chestPresets.setOnClickListener {
            getPresetsByMuscle("chest")
        }
        binding.backPresets.setOnClickListener {
            getPresetsByMuscle("back")
        }
        binding.absPresets.setOnClickListener {
            getPresetsByMuscle("waist")
        }
        binding.shoulderPresets.setOnClickListener {
            getPresetsByMuscle("shoulders")
        }
        binding.cardioPresets.setOnClickListener {
            getPresetsByMuscle("cardio")
        }
        binding.neckPresets.setOnClickListener {
            getPresetsByMuscle("neck")
        }
        binding.upperArmsPresets.setOnClickListener {
            getPresetsByMuscle("upper arms")
        }
        binding.lowerArmsPresets.setOnClickListener {
            getPresetsByMuscle("lower arms")
        }
        binding.upperLegsPresets.setOnClickListener {
            getPresetsByMuscle("upper legs")
        }
        binding.lowerLegsPresets.setOnClickListener {
            getPresetsByMuscle("lower legs")
        }

        return view
    }

    private fun startRecyclerView() {
        adapter = PresetsAdapter(requireContext(), presetsList, this)
        binding.rvPresets.layoutManager = LinearLayoutManager(context)
        binding.rvPresets.adapter = adapter
    }

    private fun getPresetsByMuscle(muscle: String){
        CoroutineScope(Dispatchers.IO).launch{
            val apiResponse = repository.getPresets("bodyPart/$muscle")

            run { CoroutineScope(Dispatchers.Main).launch {
                if (apiResponse.isEmpty()){
                    //Error
                    binding.emptyResultImg.setImageResource(R.drawable.not_found)
                    binding.emptyResultTxt.text = "Workout not found"
                    binding.presetsEmptyLayout.visibility = View.VISIBLE
                    showError()
                } else {
                    presetsList.clear()
                    presetsList.addAll(apiResponse)
                    binding.presetsEmptyLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
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
                }
            } }

        }

    }

    private fun getPresetsBySearch(query: String){
        CoroutineScope(Dispatchers.IO).launch{
            val apiResponse = repository.getPresets("name/${query.toLowerCase()}")

            run { CoroutineScope(Dispatchers.Main).launch {
                if (apiResponse.isEmpty()){
                    //Error
                    binding.emptyResultImg.setImageResource(R.drawable.not_found)
                    binding.emptyResultTxt.text = "Workout not found"
                    binding.presetsEmptyLayout.visibility = View.VISIBLE
                    showError()
                } else {
                    presetsList.clear()
                    presetsList.addAll(apiResponse)
                    binding.presetsEmptyLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
            } }

        }

    }

    private fun showError() {
        Toast.makeText(requireContext(), "Error while fetching data", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(item: PresetsModel, adapterPosition: Int) {
//        val launchPresetDetails = Intent(this.context, InfoActivity::class.java)
//        launchPresetDetails.putExtra("selected_workout" , item as Serializable) //Save item on Intend
//        launchPresetDetails.putExtra("position" , adapterPosition as Serializable) //Save item position on Intend
//        startActivity(launchPresetDetails)
        Toast.makeText(requireContext(), item.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            getPresetsBySearch(query)
        }
        binding.searchPreset.clearFocus()
//        val keyboard = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        keyboard.hideSoftInputFromWindow(binding.searchPreset.windowToken, 0)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}
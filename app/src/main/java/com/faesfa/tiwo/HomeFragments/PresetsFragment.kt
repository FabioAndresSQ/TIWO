package com.faesfa.tiwo.HomeFragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
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
    private var tutorialStep = 0
    private var tutorialCompleted = false


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

        dataManager.checkDbDate(activity, false)


        isLoading.observe(requireActivity()) {
            binding.loadingBar.isVisible = it
            binding.rvPresets.isVisible = !it
        }

        binding.searchPreset.setOnQueryTextListener(this)
        startRecyclerView()

        if (!tutorialCompleted){

            binding.tutorialLayout.visibility = View.VISIBLE

            binding.tutorialLayout.setOnClickListener {
                tutorialStep++
                when (tutorialStep){
                    1 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.startTutorial, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialStepPager.visibility = View.VISIBLE
                            binding.topArrowLeft.visibility = View.VISIBLE
                            ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.topArrowLeft, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.tutorialLayoutBackground, "translationY", 170f).apply {
                            duration = 300
                            start()
                        }
                        }
                        binding.tutorialLayout.elevation = 14F
                    }
                    2 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.topArrowLeft, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            binding.tutorialStepPager.text = "Aqui puedes buscar por categoria para que de esa forma puedas encontrar los ejercicios que quieres por el grupo muscular"
                            ObjectAnimator.ofFloat(binding.tutorialStepPager, "translationY", 160f).apply {
                                duration = 0
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.topArrowLeft, "translationY", 160f).apply {
                                duration = 0
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.tutorialLayoutBackground, "translationY", 340f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                            ObjectAnimator.ofFloat(binding.topArrowLeft, "alpha", 1f).apply {
                                duration = 300
                                start()
                            }
                        }
                    }
                    3 -> {
                        val tutorialSwitch = ObjectAnimator.ofFloat(binding.tutorialStepPager, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        ObjectAnimator.ofFloat(binding.topArrowRight, "alpha", 0f).apply {
                            duration = 300
                            start()
                        }
                        tutorialSwitch.doOnEnd {
                            ObjectAnimator.ofFloat(binding.tutorialLayout, "alpha", 0f).apply {
                                duration = 300
                                start()
                            }
                            binding.tutorialLayout.visibility = View.GONE
                            tutorialCompleted = true

                            getPresetsByMuscle("chest")
                            assingButtonColor(binding.chestPresets)

                        }
                    }
                }
            }
        } else {
            binding.tutorialLayout.visibility = View.GONE
        }

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

    fun hasInternet(context: Context): Boolean? {
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

    private fun startRecyclerView() {
        adapter = PresetsAdapter(requireContext(), presetsList, this)
        binding.rvPresets.layoutManager = LinearLayoutManager(context)
        binding.rvPresets.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        dataManager.checkDbDate(activity, false)
    }
    private fun getPresetsByMuscle(muscle: String){
        binding.presetsEmptyLayout.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch{
            isLoading.postValue(true)
            val apiResponse = repository.getPresetsByMuscleFromDb(muscle)
            run { CoroutineScope(Dispatchers.Main).launch {
                if (apiResponse.isEmpty()){
                    //Error
                    if (hasInternet(requireContext()) == false){
                        Toast.makeText(context, getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                    } else {
                        dataManager.checkDbDate(activity, false)
                    }
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

                /*TRANSLATE RESULT ON FUTURE UPDATE*/
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
                    if (hasInternet(requireContext()) == false){
                        Toast.makeText(context, getString(R.string.errorConnectingToApiToast), Toast.LENGTH_SHORT).show()
                    }
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
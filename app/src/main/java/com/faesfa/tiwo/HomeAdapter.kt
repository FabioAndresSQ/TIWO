package com.faesfa.tiwo

import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.faesfa.tiwo.HomeFragments.MainFragment
import com.faesfa.tiwo.HomeFragments.PresetsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class HomeAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {MainFragment()}
            1 -> {PresetsFragment()}
            else -> {throw NotFoundException("Position not found")}
        }
    }
}
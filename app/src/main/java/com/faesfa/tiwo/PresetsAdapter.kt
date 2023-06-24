package com.faesfa.tiwo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faesfa.tiwo.data.model.PresetsModel
import com.faesfa.tiwo.databinding.PresetsViewBinding
import com.squareup.picasso.Picasso

class PresetsAdapter (private val context: Context,
                      val items: List<PresetsModel>,
                      private val listener: OnPresetClickListener)
    :RecyclerView.Adapter<PresetsAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.presets_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        //Assign values to each view
        Picasso.get().load(item.gifUrl).into(holder.binding.presetImage)
        holder.binding.presetNameTxt.text = item.name?.uppercase()
        holder.binding.muscleTxt.text = item.bodyPart?.capitalize()
        holder.binding.targetTxt.text = item.target?.capitalize()
        holder.binding.equipmentTxt.text = item.equipment?.capitalize()
    }


    //VIEWHOLDER
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val binding = PresetsViewBinding.bind(view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val preset = items[adapterPosition]
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(preset, adapterPosition)
            }
        }
    }
    interface OnPresetClickListener{
        fun onItemClick(item: PresetsModel, adapterPosition: Int)
    }
}


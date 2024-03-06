package com.faesfa.tiwo

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.faesfa.tiwo.databinding.PresetsViewBinding
import com.faesfa.tiwo.domain.model.Preset


class PresetsAdapter (private val context: Context,
                      val items: List<Preset>,
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
        holder.binding.loadingPresetImage.visibility = View.VISIBLE
        Glide.with(this.context).asGif()
            .load(item.gifUrl)
            .listener(object : RequestListener<GifDrawable?> {

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingPresetImage.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.loadingPresetImage.visibility = View.VISIBLE
                    return false
                }

            }).into(holder.binding.presetImage)

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
        fun onItemClick(item: Preset, adapterPosition: Int)
    }
}



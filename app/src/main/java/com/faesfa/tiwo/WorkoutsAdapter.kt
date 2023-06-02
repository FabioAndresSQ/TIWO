package com.faesfa.tiwo

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.faesfa.tiwo.databinding.ItemViewBinding

class WorkoutsAdapter(
    private val context: Context, val items: ArrayList<WorkoutsModelClass>,
    private val listener: OnItemClickListener):
    RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)
        )}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Assign the values to show on screen
        val item = items[position]

        when (item.category){
            "Chest" -> {holder.binding.workoutImage.setImageResource(R.drawable.chest_ic)}
            "Back" -> {holder.binding.workoutImage.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {holder.binding.workoutImage.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {holder.binding.workoutImage.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {holder.binding.workoutImage.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {holder.binding.workoutImage.setImageResource(R.drawable.abs_ic)}
        }
        val dataManager = DataManager()


        @RequiresApi(Build.VERSION_CODES.M)
        holder.binding.workoutImage.borderColor = context.getColor(R.color.AppColor)
        holder.binding.workoutImage.borderWidth = 5

        holder.binding.workoutNameTxt.text = item.name.uppercase()
        holder.binding.setsTxt.text = item.sets.toString()
        //Check if working with Reps or Time
        if (item.reps){
            holder.binding.workWithTxt.text = context.getString(R.string.itemRepsTxt)
            holder.binding.repsTxt.text = item.num_reps.toString()
            holder.binding.workoutDurationTxt.text = dataManager.convertTime(((item.reps_time * item.num_reps + item.rest_time) * item.sets).toInt())
        }else{
            val repsText = dataManager.convertTime(item.work_time)
            holder.binding.workWithTxt.text = context.getString(R.string.itemWorkTxt)
            holder.binding.repsTxt.text = repsText
            holder.binding.workoutDurationTxt.text = dataManager.convertTime((item.work_time + item.rest_time) * item.sets)//convertTime((item.work_time + item.rest_time) * item.sets)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val binding = ItemViewBinding.bind(view)
        //val workoutImage: ImageView = view.findViewById(R.id.workoutImage)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = items[adapterPosition]
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(item, adapterPosition)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(item: WorkoutsModelClass, adapterPosition: Int)
    }

}
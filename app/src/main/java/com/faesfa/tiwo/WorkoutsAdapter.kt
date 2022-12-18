package com.faesfa.tiwo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutsAdapter(
    private val context: Context, val items: ArrayList<WorkoutsModelClass>,
    private val listener: OnItemClickListener):
    RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)
        )}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val min : String
        var secs : String
        //holder.workoutImage.setImageResource(R.drawable.ic_launcher_foreground)

        holder.workoutName.text = item.name
        holder.sets.text = item.sets.toString()
        //Check if working with Reps or Time
        if (item.reps){
            holder.workWith.text = context.getString(R.string.workingReps)
            holder.reps.text = item.num_reps.toString()
            holder.workoutDuration.text = convertTime(((item.reps_time * item.num_reps + item.rest_time) * item.sets).toInt())
        }else{
            val repsText = item.work_time.toString() + " secs"
            holder.workWith.text = context.getString(R.string.workingTime)
            holder.reps.text = repsText
            holder.workoutDuration.text = convertTime((item.work_time + item.rest_time) * item.sets)
        }
    }

    private fun convertTime(secs: Int): String {
        val minutes = secs / 60
        var minutesTxt = ""
        val seconds = secs % 60
        var secondsTxt = ""
        minutesTxt = if (minutes < 10){
            "0$minutes"
        } else {
            "$minutes"
        }
        secondsTxt = if (seconds < 10){
            "0$seconds"
        } else {
            "$seconds"
        }
        return "$minutesTxt:$secondsTxt"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        //val workoutImage: ImageView = view.findViewById(R.id.workoutImage)
        val workoutName: TextView = view.findViewById(R.id.workoutNameTxt)
        val sets: TextView = view.findViewById(R.id.setsTxt)
        val workWith: TextView = view.findViewById(R.id.workWithTxt)
        val reps: TextView = view.findViewById(R.id.repsTxt)
        val workoutDuration: TextView = view.findViewById(R.id.workoutDurationTxt)

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
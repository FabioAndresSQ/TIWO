package com.faesfa.tiwo

import android.content.Context
import android.os.Build
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

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
            "Chest" -> {holder.workoutImage.setImageResource(R.drawable.chest_ic)}
            "Back" -> {holder.workoutImage.setImageResource(R.drawable.back_ic)}
            "Shoulder" -> {holder.workoutImage.setImageResource(R.drawable.shoulder_ic)}
            "Arms" -> {holder.workoutImage.setImageResource(R.drawable.arm_ic)}
            "Legs" -> {holder.workoutImage.setImageResource(R.drawable.legs_ic)}
            "Abs" -> {holder.workoutImage.setImageResource(R.drawable.abs_ic)}
        }
        val dataManager = DataManager()


        @RequiresApi(Build.VERSION_CODES.M)
        holder.workoutImage.borderColor = context.getColor(R.color.AppBlue)
        holder.workoutImage.borderWidth = 10

        holder.workoutName.text = item.name.uppercase()
        holder.sets.text = item.sets.toString()
        //Check if working with Reps or Time
        if (item.reps){
            holder.workWith.text = context.getString(R.string.workingReps)
            holder.reps.text = item.num_reps.toString()
            holder.workoutDuration.text = dataManager.convertTime(((item.reps_time * item.num_reps + item.rest_time) * item.sets).toInt())
        }else{
            val repsText = dataManager.convertTime(item.work_time)
            holder.workWith.text = context.getString(R.string.workingTime)
            holder.reps.text = repsText
            holder.workoutDuration.text = dataManager.convertTime((item.work_time + item.rest_time) * item.sets)//convertTime((item.work_time + item.rest_time) * item.sets)
        }
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
        val workoutImage: CircleImageView = view.findViewById(R.id.workoutImage)

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
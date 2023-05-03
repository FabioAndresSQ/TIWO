package com.faesfa.tiwo

import java.io.Serializable

data class Workouts(
    val workouts : ArrayList<WorkoutsModelClass>
): Serializable

data class WorkoutsModelClass(
    var name: String,
    var sets: Int,
    var reps: Boolean,
    var num_reps: Int,
    var reps_time: Double,
    var work_time: Int,
    var rest_time: Int,
    var category: String
): Serializable

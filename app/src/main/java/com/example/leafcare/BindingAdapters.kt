package com.example.leafcare

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.model.Plant
import com.example.leafcare.data.model.PlantNote
import com.example.leafcare.data.model.PlantSchedule
import com.example.leafcare.data.model.PlantType
import com.example.leafcare.ui.dashboard.adapters.PlantTypeLinearAdapter
import com.example.leafcare.ui.home.adapters.NoteLinearAdapter
import com.example.leafcare.ui.home.adapters.PlantLinearAdapter

@BindingAdapter("authApiStatus")
fun bindAuthStatus(statusLinearLayout: LinearLayout,
    status: ApiStatus?) {

    val pb = statusLinearLayout.findViewById<ProgressBar>(R.id.pb)
    val llLogin = statusLinearLayout.findViewById<LinearLayout>(R.id.ll_auth)

    when (status) {
        ApiStatus.LOADING -> {
            pb.visibility = View.VISIBLE
            llLogin.visibility = View.GONE
        }
        // DONE, ERROR, Null
        else -> {
            pb.visibility = View.GONE
            llLogin.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("plantApiStatus")
fun bindPlantStatus(statusLinearLayout: LinearLayout,
               status: ApiStatus?) {

    val tvError = statusLinearLayout.findViewById<TextView>(R.id.tv_error)
    val bRetry = statusLinearLayout.findViewById<Button>(R.id.b_retry)
    val rvMyPlants = statusLinearLayout.findViewById<RecyclerView>(R.id.rv_my_plants)

    val llPlants = statusLinearLayout.findViewById<LinearLayout>(R.id.ll_plants)
    val pb = statusLinearLayout.findViewById<ProgressBar>(R.id.pb)


    when (status) {
        ApiStatus.LOADING -> {
            pb.visibility = View.VISIBLE
            llPlants.visibility = View.GONE
        }
        ApiStatus.DONE -> {
            pb.visibility = View.GONE
            llPlants.visibility = View.VISIBLE
            rvMyPlants.visibility = View.VISIBLE
            tvError.visibility = View.GONE
            bRetry.visibility = View.GONE
        }
        // ERROR or null
        else -> {
            pb.visibility = View.GONE
            llPlants.visibility = View.VISIBLE
            rvMyPlants.visibility = View.GONE
            tvError.visibility = View.VISIBLE
            bRetry.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("noteApiStatus")
fun bindNoteStatus(statusLinearLayout: LinearLayout,
                    status: ApiStatus?) {

    val tvError = statusLinearLayout.findViewById<TextView>(R.id.tv_error)
    val llNotes = statusLinearLayout.findViewById<LinearLayout>(R.id.ll_notes)
    val pb = statusLinearLayout.findViewById<ProgressBar>(R.id.pb)


    when (status) {
        ApiStatus.LOADING -> {
            pb.visibility = View.VISIBLE
            llNotes.visibility = View.GONE
            tvError.visibility = View.GONE
        }
        ApiStatus.DONE -> {
            pb.visibility = View.GONE
            llNotes.visibility = View.VISIBLE
            tvError.visibility = View.GONE
        }
        // ERROR or null
        else -> {
            pb.visibility = View.GONE
            llNotes.visibility = View.GONE
            tvError.visibility = View.VISIBLE
        }
    }
}

// pass data to list adapter
@BindingAdapter("plantListData")
fun bindPlantRecyclerView(recyclerView: RecyclerView,
                     data: List<Plant>?) {
    val adapter = recyclerView.adapter as PlantLinearAdapter
    adapter.submitList(data)
}

// pass data to list adapter
@BindingAdapter("noteListData")
fun bindNoteRecyclerView(recyclerView: RecyclerView,
                     data: List<PlantNote>?) {
    val adapter = recyclerView.adapter as NoteLinearAdapter
    adapter.submitList(data)
}

// pass data to list adapter
@BindingAdapter("scheduleListData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<PlantType>?) {
//    val adapter = recyclerView.adapter as ScheduleLinearAdapter
    val adapter = recyclerView.adapter as PlantTypeLinearAdapter
    adapter.submitList(data)
}

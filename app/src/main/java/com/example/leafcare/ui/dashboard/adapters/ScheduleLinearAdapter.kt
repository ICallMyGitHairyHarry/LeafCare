package com.example.leafcare.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leafcare.data.model.PlantSchedule
import com.example.leafcare.databinding.VerticalListItemScheduleBinding

//class ScheduleLinearAdapter:
//    ListAdapter<PlantSchedule, ScheduleLinearAdapter.ScheduleViewHolder>(DiffCallback) {
//
//    class ScheduleViewHolder (private var binding: VerticalListItemScheduleBinding):
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(plantSchedule: PlantSchedule) {
//            binding.plantSchedule = plantSchedule
//            binding.executePendingBindings()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return ScheduleViewHolder(
//            VerticalListItemScheduleBinding.inflate(layoutInflater, parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
//        val plantItem = getItem(position)
//        holder.bind(plantItem)
//    }
//
//    companion object DiffCallback : DiffUtil.ItemCallback<PlantSchedule>() {
//
//        override fun areItemsTheSame(oldItem: PlantSchedule, newItem: PlantSchedule): Boolean {
//            return oldItem.plantType == newItem.plantType
//        }
//
//        override fun areContentsTheSame(oldItem: PlantSchedule, newItem: PlantSchedule): Boolean {
//            return oldItem.date == newItem.date && oldItem.plantType == newItem.plantType
//        }
//    }
//}
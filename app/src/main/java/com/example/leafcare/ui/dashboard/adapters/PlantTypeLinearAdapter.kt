package com.example.leafcare.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leafcare.data.model.PlantSchedule
import com.example.leafcare.data.model.PlantType
import com.example.leafcare.databinding.VerticalListItemScheduleBinding

class PlantTypeLinearAdapter:
    ListAdapter<PlantType, PlantTypeLinearAdapter.PlantTypeViewHolder>(DiffCallback) {

    class PlantTypeViewHolder (private var binding: VerticalListItemScheduleBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plantType: PlantType) {
            binding.plantType = plantType
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantTypeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlantTypeViewHolder(
            VerticalListItemScheduleBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlantTypeViewHolder, position: Int) {
        val plantItem = getItem(position)
        holder.bind(plantItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlantType>() {

        override fun areItemsTheSame(oldItem: PlantType, newItem: PlantType): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PlantType, newItem: PlantType): Boolean {
            return oldItem.name == newItem.name && oldItem.description == newItem.description
        }
    }
}
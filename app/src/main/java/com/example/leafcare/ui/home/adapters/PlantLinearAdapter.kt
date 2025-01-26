package com.example.leafcare.ui.home.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leafcare.data.model.Plant
import com.example.leafcare.databinding.VerticalListItemMyPlantBinding

class PlantLinearAdapter (private val clickListener: PlantListener):
    ListAdapter<Plant, PlantLinearAdapter.PlantViewHolder>(DiffCallback) {

    class PlantViewHolder (private var binding: VerticalListItemMyPlantBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: PlantListener, plant: Plant) {
            binding.plant = plant
            val bitmap = convertBase64ToBitmap(plant.photo)
            binding.ivPlant.setImageBitmap(bitmap)
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        private fun convertBase64ToBitmap(base64String: String): Bitmap {
            val cleanedBase64 = base64String.replace("\n", "").replace("\\n", "")
            val decodedBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlantViewHolder(
            VerticalListItemMyPlantBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plantItem = getItem(position)
        holder.bind(clickListener, plantItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Plant>() {

        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.name == newItem.name && oldItem.plantType == newItem.plantType
        }
    }

}

class PlantListener(val clickFunction: (plant: Plant) -> Unit) {
    fun onClick(plant: Plant) = clickFunction(plant)
}
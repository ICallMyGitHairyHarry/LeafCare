package com.example.leafcare.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.leafcare.data.model.PlantNote
import com.example.leafcare.databinding.VerticalListItemNoteBinding

class NoteLinearAdapter (private val clickListener: NoteListener):
    ListAdapter<PlantNote, NoteLinearAdapter.NoteViewHolder>(DiffCallback) {

    class NoteViewHolder (private var binding: VerticalListItemNoteBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: NoteListener, note: PlantNote) {
            binding.note = note
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(
            VerticalListItemNoteBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteItem = getItem(position)
        holder.bind(clickListener, noteItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlantNote>() {

        override fun areItemsTheSame(oldItem: PlantNote, newItem: PlantNote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantNote, newItem: PlantNote): Boolean {
            return oldItem.logDate == newItem.logDate
        }
    }
}

class NoteListener(val clickFunction: (note: PlantNote) -> Unit) {
    fun onClick(note: PlantNote) = clickFunction(note)
}
package com.example.ma

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeSlotAdapter(
    var timeSlots: MutableList<TimeSlot>,
    private val onBookClick: (TimeSlot, Button) -> Unit // Pass the button too
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    class TimeSlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.sessionDate)
        val timeTextView: TextView = view.findViewById(R.id.sessionTime)
        val bookButton: Button = view.findViewById(R.id.bookSessionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.dateTextView.text = timeSlot.date
        holder.timeTextView.text = timeSlot.time

        // Reset the state of the button when it gets recycled
        holder.bookButton.text = "Book"
        holder.bookButton.isEnabled = true

        // Set a click listener for the booking button
        holder.bookButton.setOnClickListener {
            // Invoke the onBookClick lambda and pass the current TimeSlot and the button
            onBookClick(timeSlot, holder.bookButton)
        }
    }

    override fun getItemCount() = timeSlots.size
}

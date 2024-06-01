package com.example.ma

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ma.R

class StudentCounselorAdapter(
    private val counselorList: List<Counselor>,
    private val onTimeSlotClick: (String) -> Unit

) : RecyclerView.Adapter<StudentCounselorAdapter.CounselorViewHolder>() {
    class CounselorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counselorName: TextView = itemView.findViewById(R.id.counselorName)
        val counselorEmail: TextView = itemView.findViewById(R.id.counselorEmail)
        val counselorDescription: TextView = itemView.findViewById(R.id.counselorDescription)
        val availableTimeSlotsButton: Button = itemView.findViewById(R.id.availableTimeSlotsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounselorViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.working_counselor_layout, parent, false)
        return CounselorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CounselorViewHolder, position: Int) {
        val currentCounselor = counselorList[position]

        holder.counselorName.text = "Name: ${currentCounselor.name}"
        holder.counselorEmail.text = "Email: ${currentCounselor.email}"
        holder.counselorDescription.text = "Description: ${currentCounselor.description}"

        // Set click listener for the "Available Time Slots" button here
        holder.availableTimeSlotsButton.setOnClickListener {
            onTimeSlotClick(currentCounselor.id)
        }
    }

    override fun getItemCount(): Int {
        return counselorList.size
    }
}


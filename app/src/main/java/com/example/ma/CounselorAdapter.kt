package com.example.ma

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CounselorAdapter(
    private val context: Context,
    private val counselorList: MutableList<Counselor>
) : RecyclerView.Adapter<CounselorAdapter.ViewHolder>() {

    var onDeleteClickListener: ((String) -> Unit)? = null
    var onAddTimeClickListener: ((String) -> Unit)? = null
    var onUpdateClickListener: ((String) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCounselorName: TextView = itemView.findViewById(R.id.tvCounselorName)
        val tvCounselorEmail: TextView = itemView.findViewById(R.id.tvCounselorEmail)
        val tvCounselorDescription: TextView = itemView.findViewById(R.id.tvCounselorDescription)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnAddTime: ImageButton = itemView.findViewById(R.id.btnAddTimeSlot)
        val btnUpdate: ImageButton = itemView.findViewById(R.id.btnUpdateInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_counselor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val counselor = counselorList[position]

        holder.tvCounselorName.text = "Name: ${counselor.name}"
        holder.tvCounselorEmail.text = "E-mail: ${counselor.email}"
        holder.tvCounselorDescription.text = "Description: ${counselor.description}"

        // Delete Button Click Listener
        holder.btnDelete.setOnClickListener {
            val counselorId = counselor.id
            onDeleteClickListener?.invoke(counselorId)
        }

        // Add Time Button Click Listener
        holder.btnAddTime.setOnClickListener {
            val counselorId = counselor.id
            onAddTimeClickListener?.invoke(counselorId)
        }

        // Update Button Click Listener
        holder.btnUpdate.setOnClickListener {
            val counselorId = counselor.id
            onUpdateClickListener?.invoke(counselorId)
        }
    }

    override fun getItemCount(): Int {
        return counselorList.size
    }
}

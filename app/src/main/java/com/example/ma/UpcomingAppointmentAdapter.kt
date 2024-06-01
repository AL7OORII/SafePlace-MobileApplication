package com.example.ma

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ma.databinding.ComingappointmentItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpcomingAppointmentAdapter(
    private val context: Context,
    private var appointments: List<Appointment>,
    private val onMessageClick: (Appointment) -> Unit,
    private val onCancelClick: (Appointment) -> Unit
) : RecyclerView.Adapter<UpcomingAppointmentAdapter.AppointmentViewHolder>() {

    private val counselorNameCache = mutableMapOf<String, String>()

    class AppointmentViewHolder(val binding: ComingappointmentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ComingappointmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        with(holder.binding) {
            tvAppointmentDate.text = "Date: ${appointment.date}"
            tvAppointmentTime.text = "Time: ${appointment.time}"
            updateNameText(appointment.counselorId, tvCounselorName, "Counselor Name")


            btnSendMessage.setOnClickListener { onMessageClick(appointment) }
            btnCancelAppointment.setOnClickListener { onCancelClick(appointment) }
        }
    }

    private fun updateNameText(id: String, textView: TextView, label: String) {
        textView.text = "$label: ${counselorNameCache[id] ?: "Fetching..."}"
        if (id !in counselorNameCache) {
            fetchName("Counselors", id) { name ->
                counselorNameCache[id] = name
                (context as? AppCompatActivity)?.runOnUiThread {
                    textView.text = "$label: $name"
                }
            }
        }
    }

    private fun fetchName(node: String, id: String,  callback: (String) -> Unit) {
        FirebaseDatabase.getInstance().getReference("$node/$id/name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    name?.let { callback(it) } ?: callback("Unknown")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch counselor name", Toast.LENGTH_SHORT).show()
                    callback("Unknown")
                }
            })

    }

    override fun getItemCount() = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}

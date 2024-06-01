package com.example.ma
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ma.databinding.ManageAppointmentItemBinding
import com.google.firebase.database.*

class AppointmentAdapter(
    private val context: Context,
    private val appointments: MutableList<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private val studentNameCache = mutableMapOf<String, String>()
    private val counselorNameCache = mutableMapOf<String, String>()

    class AppointmentViewHolder(val binding: ManageAppointmentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ManageAppointmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        with(holder.binding) {
            appointmentDate.text = "Date: ${appointment.date}"
            appointmentTime.text = "Time: ${appointment.time}"

            updateNameText(appointment.studentUid, holder.binding.StudentName, "Student Name", isCounselor = false)
            updateNameText(appointment.counselorId, holder.binding.CounselorName, "Counselor Name", isCounselor = true)

            btnApproveAppointment.setOnClickListener {
                approveAppointment(appointment, position)
            }

            btnCancelAppointment.setOnClickListener {
                cancelAppointment(appointment, position)
            }
        }


    }

    private fun updateNameText(uid: String, textView: TextView, label: String, isCounselor: Boolean) {
        val cache = if (isCounselor) counselorNameCache else studentNameCache
        val nodeName = if (isCounselor) "Counselors" else "Users"

        textView.text = "$label: ${cache[uid] ?: "Fetching..."}"
        if (uid !in cache) {
            fetchName(nodeName, uid) { name ->
                cache[uid] = name
                (context as? AppCompatActivity)?.runOnUiThread {
                    textView.text = "$label: $name"
                }
            }
        }
    }

    private fun fetchName(node: String, uid: String, callback: (String) -> Unit) {
        FirebaseDatabase.getInstance().getReference("$node/$uid/name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    callback(name ?: "Unknown")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch name", Toast.LENGTH_SHORT).show()
                    callback("Unknown")
                }
            })
    }


    private fun approveAppointment(appointment: Appointment, position: Int) {
        val appointmentRef = FirebaseDatabase.getInstance().getReference("pendingAppointments/${appointment.id}")
        appointmentRef.child("status").setValue("approved")
            .addOnSuccessListener {
                val timeSlotRef = FirebaseDatabase.getInstance().getReference("Counselors/${appointment.counselorId}/timeSlots/${appointment.timeslotId}")
                timeSlotRef.removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Appointment approved and time slot removed", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to remove time slot", Toast.LENGTH_SHORT).show()
                }

                val upcomingAppointmentsRef = FirebaseDatabase.getInstance().getReference("Users/${appointment.studentUid}/upcomingAppointments")
                val newUpcomingAppointmentRef = upcomingAppointmentsRef.push()
                newUpcomingAppointmentRef.setValue(appointment).addOnSuccessListener {
                    Toast.makeText(context, "Appointment moved to student's upcoming appointments", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to move appointment", Toast.LENGTH_SHORT).show()
                }

                appointments.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to approve appointment", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelAppointment(appointment: Appointment, position: Int) {
        val appointmentRef = FirebaseDatabase.getInstance().getReference("pendingAppointments/${appointment.id}")
        appointmentRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT).show()
                appointments.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int = appointments.size



    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments.clear()
        appointments.addAll(newAppointments)
        notifyDataSetChanged()
    }
}
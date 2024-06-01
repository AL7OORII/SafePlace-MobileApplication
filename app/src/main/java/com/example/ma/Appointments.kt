package com.example.ma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Appointments : AppCompatActivity() {

    private lateinit var upcomingAppointmentsRecyclerView: RecyclerView
    private lateinit var upcomingAppointmentsAdapter: UpcomingAppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        upcomingAppointmentsRecyclerView = findViewById(R.id.upcomingAppointmentsRecyclerView)
        upcomingAppointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        upcomingAppointmentsAdapter = UpcomingAppointmentAdapter(this,
            mutableListOf(),
            onMessageClick = { appointment ->
                val intent = Intent(this, SendMessage::class.java)
                intent.putExtra("CounselorId", appointment.counselorId) // Pass counselor ID to the SendMessage activity
                startActivity(intent)
            },
            onCancelClick = { appointment ->
                cancelAppointment(appointment)
            }
        )
        upcomingAppointmentsRecyclerView.adapter = upcomingAppointmentsAdapter
        loadUpcomingAppointments()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadUpcomingAppointments() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            val upcomingAppointmentsRef = FirebaseDatabase.getInstance().getReference("Users/$uid/upcomingAppointments")
            upcomingAppointmentsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val upcomingAppointments = mutableListOf<Appointment>()
                    for (appointmentSnapshot in snapshot.children) {
                        val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                        appointment?.let {
                            it.id = appointmentSnapshot.key ?: ""
                            upcomingAppointments.add(it)
                        }
                    }
                    upcomingAppointmentsAdapter.updateAppointments(upcomingAppointments)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Appointments, "Error loading upcoming appointments: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun cancelAppointment(appointment: Appointment) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val ref = FirebaseDatabase.getInstance().getReference("Users/$userId/upcomingAppointments/${appointment.id}")
            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Appointment cancelled", Toast.LENGTH_SHORT).show()
                    loadUpcomingAppointments() // Reload appointments to update the UI
                } else {
                    Toast.makeText(this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

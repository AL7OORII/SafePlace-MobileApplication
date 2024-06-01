package com.example.ma

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageAppointments : AppCompatActivity() {

    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_appointments)

        appointmentsRecyclerView = findViewById(R.id.recyclerViewAppointments)
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        appointmentAdapter = AppointmentAdapter(this, mutableListOf())
        appointmentsRecyclerView.adapter = appointmentAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("pendingAppointments")
        loadAppointments()


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // End this activity and go back to the previous one
        }
    }

    private fun loadAppointments() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedAppointments = mutableListOf<Appointment>()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)?.apply {
                        id = appointmentSnapshot.key ?: ""
                    }
                    appointment?.let { updatedAppointments.add(it) }
                }
                appointmentAdapter.updateAppointments(updatedAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ManageAppointments,
                    "Error loading appointments: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


    private fun fetchStudentName(uid: String, onComplete: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                onComplete(name)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                onComplete("Unknown")
            }
        })
    }

    private fun fetchCounselorName(counselorId: String, onComplete: (String) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Counselors/$counselorId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                onComplete(name)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                onComplete("Unknown")
            }
        })
    }
}



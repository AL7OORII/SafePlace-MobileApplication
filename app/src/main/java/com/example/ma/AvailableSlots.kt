package com.example.ma

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AvailableSlots : AppCompatActivity() {

    private lateinit var slotsRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var counselorId: String
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_slots)

        // Retrieve the current user's UID
        uid = FirebaseAuth.getInstance().currentUser?.uid

        // Retrieve counselorId from the intent
        counselorId = intent.getStringExtra("counselorId") ?: return

        slotsRecyclerView = findViewById(R.id.slotsRecyclerView)
        slotsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")
            .child(counselorId).child("timeSlots")


        timeSlotAdapter = TimeSlotAdapter(mutableListOf()) { timeSlot, bookButton ->
            // Book the time slot and set the button to "Pending"
            bookTimeSlot(timeSlot, bookButton)
        }
        slotsRecyclerView.adapter = timeSlotAdapter

        loadTimeSlots()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // End this activity and go back to the previous one
        }
    }

    private fun bookTimeSlot(timeSlot: TimeSlot, bookButton: Button) {
        uid?.let { uid ->
            val newAppointment = Appointment(
                timeslotId = timeSlot.id,
                studentUid = uid,
                counselorId = counselorId,
                date = timeSlot.date,
                time = timeSlot.time,
                status = "pending",
            )
            val appointmentRef = FirebaseDatabase.getInstance().getReference("pendingAppointments").push()
            appointmentRef.setValue(newAppointment)
                .addOnSuccessListener {
                    // Set the button to show "Pending" and disable itv
                    bookButton.text = "Pending"
                    bookButton.isEnabled = false
                    Toast.makeText(this, "Appointment booked. Waiting for approval.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to book appointment.", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User must be logged in to book appointments.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadTimeSlots() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeSlots = mutableListOf<TimeSlot>()
                for (timeSlotSnapshot in snapshot.children) {
                    val timeSlot = timeSlotSnapshot.getValue(TimeSlot::class.java)
                    timeSlot?.let {
                        // Create a new TimeSlot instance with the updated ID
                        val updatedTimeSlot = it.copy(id = timeSlotSnapshot.key ?: "")
                        timeSlots.add(updatedTimeSlot)
                    }
                }
                timeSlotAdapter.timeSlots = timeSlots
                timeSlotAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AvailableSlots, "Failed to load time slots.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}


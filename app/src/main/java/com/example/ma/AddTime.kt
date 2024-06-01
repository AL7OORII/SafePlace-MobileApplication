package com.example.ma

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddTime : AppCompatActivity() {

    private lateinit var counselorId: String
    private lateinit var counselorName: String
    private lateinit var dateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var addButton: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_time)

        // Retrieve counselor details from extras
        counselorId = intent.getStringExtra("counselorId") ?: ""
        counselorName = intent.getStringExtra("counselorName") ?: ""

        // Initialize UI elements
        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)
        addButton = findViewById(R.id.addButton)

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")

        // Set a click listener for the "Add" button
        addButton.setOnClickListener {
            // Handle the add time logic here
            addTime()
        }

        // Set the page title to include the counselor's name
        supportActionBar?.title = "Add Time for $counselorName"

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addTime() {
        // Retrieve the selected date and time
        val selectedDate = dateInput.text.toString().trim()
        val selectedTime = timeInput.text.toString().trim()

        // Validate input (you can add more validation as needed)
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            // Display an error message if any field is empty
            Toast.makeText(this, "Please enter both date and time", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new time slot entry for the counselor
        val timeSlot = TimeSlot(id = counselorId, date = selectedDate, time = selectedTime)

        // Add the new time slot to the counselor's database node
        databaseReference.child(counselorId).child("timeSlots").push().setValue(timeSlot)
            .addOnSuccessListener {
                // Time slot added successfully
                Toast.makeText(this, "Time slot added for $counselorName", Toast.LENGTH_SHORT).show()
                finish() // Finish the activity
            }
            .addOnFailureListener { e ->
                // Handle any errors during the time slot addition
                Toast.makeText(this, "Error adding time slot", Toast.LENGTH_SHORT).show()
            }
    }
}

data class TimeSlot(var id: String = "", val date: String = "", val time: String = "")
 {
}



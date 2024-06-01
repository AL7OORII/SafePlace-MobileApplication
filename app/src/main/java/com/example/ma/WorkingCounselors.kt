package com.example.ma

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WorkingCounselors : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var counselorList: MutableList<Counselor>
    private lateinit var StudentCounselorAdapter: StudentCounselorAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_working_counselors)

        recyclerView = findViewById(R.id.studentcounselorsRecyclerView)
        counselorList = mutableListOf()
        StudentCounselorAdapter = StudentCounselorAdapter(counselorList) { counselorId ->
            navigateToAvailableSlots(counselorId)
        }

        recyclerView.adapter = StudentCounselorAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")

        loadCounselorData()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun navigateToAvailableSlots(counselorId: String) {
        // Find the counselor object with the matching counselorId
        val selectedCounselor = counselorList.find { it.id == counselorId }

        if (selectedCounselor != null) {
            val intent = Intent(this, AvailableSlots::class.java)
            // Pass the selected counselor's information as extras to the intent
            intent.putExtra("counselorId", selectedCounselor.id)
            intent.putExtra("counselorName", selectedCounselor.name)
            intent.putExtra("counselorDescription", selectedCounselor.description)
            startActivity(intent)
        } else {
            // Handle the case where the counselor with counselorId was not found
            // Display a toast message to inform the user
            Toast.makeText(this, "not time slots found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadCounselorData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                counselorList.clear()

                for (counselorSnapshot in snapshot.children) {
                    val counselor = counselorSnapshot.getValue(Counselor::class.java)
                    counselor?.let {
                        counselorList.add(it)
                    }
                }

                StudentCounselorAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
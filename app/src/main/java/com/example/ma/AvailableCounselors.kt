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

class AvailableCounselors : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var counselorList: MutableList<Counselor>
    private lateinit var counselorAdapter: CounselorAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_counselors)

        recyclerView = findViewById(R.id.counselorsRecyclerView)
        counselorList = mutableListOf()
        counselorAdapter = CounselorAdapter(this, counselorList)

        // Set click listeners for delete, add time, and update buttons
        counselorAdapter.onDeleteClickListener = { counselorId ->
            deleteCounselor(counselorId)
        }

        counselorAdapter.onAddTimeClickListener = { counselorId ->
            navigateToAddTime(counselorId)
        }

        counselorAdapter.onUpdateClickListener = { counselorId ->
            navigateToUpdateCounselor(counselorId)
        }

        recyclerView.adapter = counselorAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")

        loadCounselorData()

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun navigateToUpdateCounselor(counselorId: String) {
        // Find the counselor object with the matching counselorId
        val selectedCounselor = counselorList.find { it.id == counselorId }

        if (selectedCounselor != null) {
            val intent = Intent(this, UpdateCounselor::class.java)
            // Pass the selected counselor's data as extras to the intent
            intent.putExtra("counselorId", selectedCounselor.id)
            intent.putExtra("counselorName", selectedCounselor.name)
            intent.putExtra("counselorEmail", selectedCounselor.email)
            intent.putExtra("counselorDescription", selectedCounselor.description)

            // Start the UpdateCounselor activity
            startActivity(intent)
        } else {
                    // Handle the case where the counselor with counselorId was not found
                    // Display a toast message to inform the user
                    Toast.makeText(this, "Counselor not found", Toast.LENGTH_SHORT).show()
                }
    }

    private fun navigateToAddTime(counselorId: String) {
        // Find the counselor object with the matching counselorId
        val selectedCounselor = counselorList.find { it.id == counselorId }

        if (selectedCounselor != null) {
            val intent = Intent(this, AddTime::class.java)
            // Pass the selected counselor's information as extras to the intent
            intent.putExtra("counselorId", selectedCounselor.id)
            intent.putExtra("counselorName", selectedCounselor.name)
            intent.putExtra("counselorDescription", selectedCounselor.description)
            startActivity(intent)
        } else {
            // Handle the case where the counselor with counselorId was not found
            // Display a toast message to inform the user
            Toast.makeText(this, "Counselor not found", Toast.LENGTH_SHORT).show()
        }
    }



    private fun deleteCounselor(counselorId: String) {
                // Get a reference to the Firebase database node for counselors
                val ref = FirebaseDatabase.getInstance().getReference("Counselors")

                // Use removeValue to delete the counselor node with counselorId
                ref.child(counselorId).removeValue()
                    .addOnSuccessListener {
                        // Counselor deleted successfully
                        // Now update the counselorList and refresh the UI
                        updateCounselorList(counselorId)
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors during deletion

                    }
            }

            private fun updateCounselorList(counselorId: String) {
                // Find and remove the deleted counselor from counselorList
                val iterator = counselorList.iterator()
                while (iterator.hasNext()) {
                    val counselor = iterator.next()
                    if (counselor.id == counselorId) {
                        iterator.remove()
                        break
                    }
                }

                // Notify the adapter to refresh the UI
                counselorAdapter.notifyDataSetChanged()
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

                        counselorAdapter.notifyDataSetChanged()
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
package com.example.ma

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ma.databinding.ActivityUpdateCounselorBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateCounselor : AppCompatActivity() {

    private lateinit var counselorId: String
    private lateinit var etCounselorName: EditText
    private lateinit var etCounselorEmail: EditText
    private lateinit var etCounselorDescription: EditText
    private lateinit var btnUpdateCounselor: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityUpdateCounselorBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCounselorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(" please wait ..")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCounselorData()

        // Retrieve counselor details from extras
        counselorId = intent.getStringExtra("counselorId")!!

        // Initialize UI elements
        etCounselorName = findViewById(R.id.etCounselorName)
        etCounselorEmail = findViewById(R.id.etCounselorEmail)
        etCounselorDescription = findViewById(R.id.etCounselorDescription)
        btnUpdateCounselor = findViewById(R.id.btnUpdateCounselor)

        // Retrieve and pre-fill counselor details
        val counselorName = intent.getStringExtra("counselorName") ?: ""
        val counselorEmail = intent.getStringExtra("counselorEmail") ?: ""
        val counselorDescription = intent.getStringExtra("counselorDescription") ?: ""
        // Initialize Firebase Realtime Database reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")

        etCounselorName.setText(counselorName)
        etCounselorEmail.setText(counselorEmail)
        etCounselorDescription.setText(counselorDescription)

        // Set a click listener for the "Update" button
        btnUpdateCounselor.setOnClickListener {
            // Handle the update logic here
            updateCounselor()

        }

        // Set a click listener for the back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadCounselorData() {

    }

    private fun updateCounselor() {

        Log.d(TAG, "updatecounselor: updating process started .. ")
        //progress
        progressDialog.setMessage("Updating Counselor info")
        progressDialog.show()

        //setup data to update to dp
        val hashMap = HashMap<String, Any>()
        hashMap["name"] = "$etCounselorName"
        hashMap["email"] = "$etCounselorEmail"
        hashMap["description"] = "$etCounselorDescription"
        hashMap["counselorId"] = "$counselorId"


        // Retrieve the updated values from the EditText fields
        val updatedName = etCounselorName.text.toString().trim()
        val updatedEmail = etCounselorEmail.text.toString().trim()
        val updatedDescription = etCounselorDescription.text.toString().trim()

        // Check if any of the fields are empty (you can add more validation if needed)
        if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map to store the updated counselor data
        val updatedCounselorData = mapOf(
            "name" to updatedName,
            "email" to updatedEmail,
            "description" to updatedDescription
        )

        // Initialize this only once globally or in onCreate
        val databaseReference = FirebaseDatabase.getInstance().getReference("Counselors")

        databaseReference.child(counselorId).updateChildren(updatedCounselorData)
            .addOnSuccessListener {
                Toast.makeText(this, "Counselor information updated", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating counselor information: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

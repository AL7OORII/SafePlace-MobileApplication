package com.example.ma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentProfile : AppCompatActivity() {
    // Define your variables, Firebase references, and views here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        // Initialize your Firebase references and views

        // Get the student's UID from Firebase Authentication
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // Query the Firebase Realtime Database to retrieve the student's data
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())

        // Add a ValueEventListener to fetch the data
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Retrieve student data from the snapshot
                    val studentID = snapshot.child("studentid").getValue(String::class.java)
                    val studentName = snapshot.child("name").getValue(String::class.java)
                    val studentEmail = snapshot.child("email").getValue(String::class.java)

                    // Get references to the TextViews in your layout
                    val studentIDText = findViewById<TextView>(R.id.studentIDText)
                    val studentNameText = findViewById<TextView>(R.id.studentNameText)
                    val studentEmailText = findViewById<TextView>(R.id.studentEmailText)

                    // Set the text of the TextViews with the retrieved data, handling null values
                    studentIDText.text = "${studentID ?: "N/A"}"
                    studentNameText.text = "${studentName ?: "N/A"}"
                    studentEmailText.text = "${studentEmail ?: "N/A"}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error, if necessary
            }
        })

        val deleteAccountButton = findViewById<Button>(R.id.deleteAccountButton)
        deleteAccountButton.setOnClickListener {
            // Get the current user
            val currentUser = FirebaseAuth.getInstance().currentUser

            // Check if the user is signed in
            if (currentUser != null) {
                // Reference to the Firebase Realtime Database
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid)

                // Attempt to delete the user's data from the Realtime Database
                databaseReference.removeValue()
                    .addOnCompleteListener { databaseTask ->
                        if (databaseTask.isSuccessful) {
                            // User's data deleted from the Realtime Database
                            // Now attempt to delete the user's account
                            currentUser.delete()
                                .addOnCompleteListener { authTask ->
                                    if (authTask.isSuccessful) {
                                        // Account deleted successfully
                                        Toast.makeText(this@StudentProfile, "Account deleted", Toast.LENGTH_SHORT).show()

                                        // Sign out the user
                                        FirebaseAuth.getInstance().signOut()

                                        // Navigate to the login or home page
                                        val intent = Intent(this@StudentProfile, StudentLoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // Failed to delete account
                                        Toast.makeText(this@StudentProfile, "Failed to delete account: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // Failed to delete user's data from the Realtime Database
                            Toast.makeText(this@StudentProfile, "Failed to delete user's data: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // User is not signed in
                Toast.makeText(this@StudentProfile, "User is not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the back button (heart icon) click to go back
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    // Override the onBackPressed method to handle the back button press
    override fun onBackPressed() {
        super.onBackPressed() // Call super.onBackPressed()
        // Handle the back button press here, for example, you can finish the activity
        finish()
    }
}

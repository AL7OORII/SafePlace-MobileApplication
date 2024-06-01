package com.example.ma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link UI elements
        val loginStudentButton: Button = findViewById(R.id.loginStudentButton)
        val loginAdminButton: Button = findViewById(R.id.loginAdminButton)

        // Set click listeners
        loginStudentButton.setOnClickListener { loginAsStudent() }
        loginAdminButton.setOnClickListener { loginAsAdmin() }
    }

    // Navigate to Student Login Activity
    private fun loginAsStudent() {
        val intent = Intent(this, StudentLoginActivity::class.java)
        startActivity(intent)
    }

    // Navigate to Admin Login Activity
    private fun loginAsAdmin() {
        val intent = Intent(this, AdminLoginActivity::class.java)
        startActivity(intent)
    }
}
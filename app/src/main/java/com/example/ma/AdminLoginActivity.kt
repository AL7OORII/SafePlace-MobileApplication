package com.example.ma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val adminEmailInput: EditText = findViewById(R.id.adminEmailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val adminLoginButton: Button = findViewById(R.id.adminLoginButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        adminLoginButton.setOnClickListener {
            val email = adminEmailInput.text.toString()
            val password = passwordInput.text.toString()
            if (email == "admin@safeplace.com" && password == "safeplace123") {
                // Logic for successful login
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // Start AdminHome activity
                val intent = Intent(this, AdminHome::class.java)
                startActivity(intent)
                finish() // Close the current activity

            } else {
                // Logic for failed login
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            // Intent to start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TOP ensures that the back stack is cleared
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
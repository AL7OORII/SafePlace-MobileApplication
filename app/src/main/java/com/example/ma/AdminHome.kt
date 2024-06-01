package com.example.ma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val manageCounselorsButton: Button = findViewById(R.id.manageCounselorsButton)
        val manageAppointmentsButton: Button = findViewById(R.id.manageAppointmentsButton)
        val viewMessagesButton: Button = findViewById(R.id.viewMessagesButton)
        val logOutButton: Button = findViewById(R.id.logOutButton)

        manageCounselorsButton.setOnClickListener {
            val intent = Intent(this, ManageCounselors::class.java)
            startActivity(intent)
        }

        manageAppointmentsButton.setOnClickListener {
            val intent = Intent(this, ManageAppointments::class.java) // Replace with actual class name
            startActivity(intent)
        }

        viewMessagesButton.setOnClickListener {
            val intent = Intent(this, Messages::class.java) // Intent to open the Messages activity
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            // Implement the logic to log out, such as clearing the user session
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

package com.example.ma

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.view.View

class ManageCounselors : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_counselors)

        val addCounselorButton: Button = findViewById(R.id.addCounselorButton)
        val viewCounselorsButton: Button = findViewById(R.id.viewCounselorsButton)

        addCounselorButton.setOnClickListener {
            // Navigate to AddCounselorActivity
            val intent = Intent(this, AddCounselor::class.java)
            startActivity(intent)
        }

        viewCounselorsButton.setOnClickListener {
            // Navigate to ViewCounselorsActivity
            val intent = Intent(this, AvailableCounselors::class.java)
            startActivity(intent)
        }
    }

    // Handle the back button click to go back to the Admin Home Page
    fun goBackToAdminHome(view: View) {
        onBackPressed()
    }
}
package com.example.ma


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.firebase.auth.FirebaseAuth
import com.example.ma.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StudentHome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeMessageTextView: TextView
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var menuIconImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        homeMessageTextView = findViewById(R.id.homeMessage)
        navView = findViewById(R.id.navView)
        menuIconImageView = findViewById(R.id.menuicon) // Find the menu icon ImageView

        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        actionBarToggle.syncState()

        // Set a click listener for the menu icon
        menuIconImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Open the drawer when the menu icon is clicked
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_view_profile -> {
                    Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StudentProfile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_find_counselors -> {
                    Toast.makeText(this, "Available Counselors", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, WorkingCounselors::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AboutUs::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_contact_us -> {
                    Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ContactUs::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val viewAppointmentsButton: Button = findViewById(R.id.viewAppointmentsButton)
        val logOutButton: Button = findViewById(R.id.logOutButton)

        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName ?: "User"
        homeMessageTextView.text = "Hi, $userName!"

        viewAppointmentsButton.setOnClickListener {
            val intent = Intent(this, Appointments::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, StudentLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        fetchUserName()

    }

    private fun fetchUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users/$uid")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "User"
                    homeMessageTextView.text = "Hi, $userName!"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StudentHome, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

package com.example.ma

import android.content.Context
import android.content.Intent
import com.example.ma.AboutUs
import com.example.ma.ContactUs
import com.example.ma.Messages
import com.example.ma.StudentProfile
import com.example.ma.WorkingCounselors

class SideMenuNavigationHandler(private val context: Context) {

    fun navigateToViewProfile() {
        val intent = Intent(context, StudentProfile::class.java)
        context.startActivity(intent)
    }

    fun navigateToFindCounselors() {
        val intent = Intent(context, WorkingCounselors::class.java)
        context.startActivity(intent)
    }


    fun navigateToAbout() {
        val intent = Intent(context, AboutUs::class.java)
        context.startActivity(intent)
    }

    fun navigateToContactUs() {
        val intent = Intent(context, ContactUs::class.java)
        context.startActivity(intent)
    }
}

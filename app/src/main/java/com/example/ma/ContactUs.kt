package com.example.ma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class ContactUs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
    }

    // Handle the back button click to go back to the previous page
    fun goBack(view: View) {
        onBackPressed()
    }
}

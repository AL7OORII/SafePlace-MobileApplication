package com.example.ma

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ma.databinding.ActivityAddCounselorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log


class AddCounselor : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAddCounselorBinding

    //Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCounselorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init //firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this )
        progressDialog.setTitle("please wait ..")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        //handle click, begin adding info
        binding.addButton.setOnClickListener{
            validateData()
        }
    }

    private var name = ""
    private var email = ""
    private var description = ""


    private fun validateData() {
        //validate data

        //get data
        name = binding.counselorNameInput.text.toString().trim()
        email = binding.counselorEmailInput.text.toString().trim()
        description = binding.counselorDescriptionInput.text.toString().trim()

        //validate data]
        if (name.isEmpty()){
            Toast.makeText(this, "Enter Counselor Name .. ", Toast.LENGTH_SHORT).show()
        }

        else if (email.isEmpty()){
            Toast.makeText(this, "Enter Counselor Email .. ", Toast.LENGTH_SHORT).show()
        }

        else if (description.isEmpty()){
            Toast.makeText(this, "Enter Counselor Description .. ", Toast.LENGTH_SHORT).show()
        }
        else{
            addCounselordataFirebase()
        }


    }

    private fun addCounselordataFirebase() {
        //show progress
        progressDialog.show()

        //get timestamp
        val timestamp = System.currentTimeMillis()

        //set up data add in Firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["description"] = description
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to firebase db: Database Root > Counselors > counselorId> counselors info
        val ref = FirebaseDatabase.getInstance().getReference("Counselors")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //when added successfully
                progressDialog.dismiss()
                Toast.makeText(this, "Counselors data were added successfully .. ", Toast.LENGTH_SHORT).show()

            }

            .addOnFailureListener { e ->
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to save data to the database: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("FirebaseError", "Firebase Database Error: ${e.message}", e)
        }


    }



}



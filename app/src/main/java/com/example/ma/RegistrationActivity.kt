package com.example.ma

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.ma.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegistrationBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account / Register user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle the back button click to go the previous screen which is the main page
        binding.backButton.setOnClickListener {
            onBackPressed()//to go the previous screen which is the main page
        }

        //handle click, begin register
        binding.signUpButton.setOnClickListener {
            /*steps
            1 - Input Data
            2 - Data Validation
            3 - Create Account - Firebase Auth
            4 - Save user Info  - Firebase Realtime Database
             */
            validateData()
        }

    }

    private var name = ""
    private var studentid = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //1 - Input Data
        name = binding.studentNameInput.text.toString().trim()
        studentid = binding.studentIdInput.text.toString().trim()
        email = binding.emailInput.text.toString().trim()
        password = binding.passwordInput.text.toString().trim()

        //2 - Data Validation
        if (name.isEmpty()) {
            // if name is empty
            Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show()
        }
        else if (studentid.isEmpty()) {
            //if student id is empty
            Toast.makeText(this, "please enter your student id", Toast.LENGTH_SHORT).show()

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            //invalid email pattern
            Toast.makeText(this, "the email you entered is not valid ", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            //empty password
            Toast.makeText(this, "please enter a password ", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //3 - Create Account - Firebase Auth

        //show progress
        progressDialog.setMessage(" Creating Account .. ")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //account created successfully, now user info will be added to the db
                updateUserInfo()
            }
            .addOnFailureListener {e->
                //failed account creation
                progressDialog.dismiss()
                Toast.makeText(this, "Account failed to create due to ${e.message} ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        //4 - Save user Info  - Firebase Realtime Database

        progressDialog.setMessage("Saving user info .. ")

        //timestamp
        val timestamp = System.currentTimeMillis()

        //get current user uid, since user is registered so we can get it now
        val uid = firebaseAuth.uid

        //setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["studentid"] = studentid
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved, the student home page will open
                progressDialog.dismiss()
                Toast.makeText(this, "Account created successfully ", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegistrationActivity, StudentHome::class.java))
                finish()

            }
            .addOnFailureListener{e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "failed saving data to db due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
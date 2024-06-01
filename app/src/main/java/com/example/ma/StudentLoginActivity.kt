package com.example.ma

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.ma.databinding.ActivityStudentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class StudentLoginActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityStudentLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while the login process / user login
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click when having to sign up
        binding.registerOption.setOnClickListener{
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        //handle click, begin login
        binding.studentLoginButton.setOnClickListener{
            /*steps
            1- input data
            2- validate data
            3- login - firebase Auth
            4- Check user type - firebase Auth
            upon successful login, the student home page will open
             */
            validateData()
        }
    }

    private var email =""
    private var password = ""

    private fun validateData() {
        // 1- input data
        email = binding.emailInput.text.toString().trim()
        password = binding.passwordInput.text.toString().trim()

        //2- validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "invalid email format", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Enter you password to log in .. ", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }

    }

    private fun loginUser() {
        //3- login - firebase Auth

        //show progress
        progressDialog.setMessage("logging in ..")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //user info saved, the student home page will open
                progressDialog.dismiss()
                Toast.makeText(this, "login was successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@StudentLoginActivity, StudentHome::class.java))
                finish()
            }
            .addOnFailureListener {e->
                //failed login
                progressDialog.dismiss()
                Toast.makeText(this, "login failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // This will navigate back to the previous page (activity) when the back button is clicked
    }


}
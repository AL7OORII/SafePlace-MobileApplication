package com.example.ma

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class SendMessage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        val messageInput: EditText = findViewById(R.id.messageInput)
        val sendButton: Button = findViewById(R.id.sendButton)
        val backButton: ImageView = findViewById(R.id.backButton)

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun sendMessage(message: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val messageRef = FirebaseDatabase.getInstance().getReference("Messages").push()
            val messageMap = hashMapOf<String, Any>(
                "userId" to user.uid,
                "message" to message,
                "timestamp" to ServerValue.TIMESTAMP, // Correctly use the Map value
                "senderName" to (user.displayName ?: "Anonymous")
            )
            messageRef.setValue(messageMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User must be logged in to send messages.", Toast.LENGTH_SHORT).show()
        }
    }

}

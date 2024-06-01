package com.example.ma

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ma.databinding.MessageItemBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Messages : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messagesAdapter = MessagesAdapter(this)
        messagesRecyclerView.adapter = messagesAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        loadMessages()
    }

    private fun loadMessages() {
        val messagesRef = FirebaseDatabase.getInstance().getReference("Messages")
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let { messages.add(it) }
                }
                messagesAdapter.updateMessages(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Messages, "Failed to load messages", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class MessagesAdapter(
    private val context: Context
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
    private var messages = listOf<Message>()
    private val senderNameCache = mutableMapOf<String, String>()

    class MessageViewHolder(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        with(holder.binding) {
            tvMessageContent.text = message.message
            updateNameText(message.userId, tvSenderName, "Sender Name")
            tvTimestamp.text = convertTimestampToString(message.timestamp)
        }
    }

    private fun updateNameText(userId: String, textView: TextView, label: String) {
        textView.text = "$label: ${senderNameCache[userId] ?: "Fetching..."}"
        if (userId !in senderNameCache) {
            fetchName("Users", userId) { name ->
                senderNameCache[userId] = name
                (context as? AppCompatActivity)?.runOnUiThread {
                    textView.text = "$label: $name"
                }
            }
        }
    }

    private fun fetchName(node: String, id: String, callback: (String) -> Unit) {
        FirebaseDatabase.getInstance().getReference("$node/$id/name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.getValue(String::class.java)
                    callback(name ?: "Unknown")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch name", Toast.LENGTH_SHORT).show()
                    callback("Unknown")
                }
            })
    }

    override fun getItemCount() = messages.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    private fun convertTimestampToString(timestamp: Long): String {
        if (timestamp == -1L) {
            return "Just now"
        }
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }
}

data class Message(
    val userId: String = "",
    val senderName: String = "",
    val message: String = "",
    val timestamp: Long = -1
)

package com.android.example.chatapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRV: RecyclerView
    private lateinit var msgBox: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var msgAdapter: MessageAdapter
    private lateinit var msgList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var senderRoom: String? = null
    var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Change the ActionBar color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid



        supportActionBar?.title = name

        chatRV = findViewById(R.id.chatRV)
        msgBox = findViewById(R.id.msgBox)
        btnSend = findViewById(R.id.btnSend)
        msgList = ArrayList()
        msgAdapter = MessageAdapter(this@ChatActivity, msgList)

        chatRV.layoutManager = LinearLayoutManager(this)
        chatRV.adapter = msgAdapter


        // Automatically scroll RecyclerView to the bottom on keyboard visibility changes
        val rootView = findViewById<RelativeLayout>(R.id.rootLayout)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val rect = Rect()
            private var lastVisibleHeight = 0

            override fun onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect)
                val visibleHeight = rect.height()

                if (lastVisibleHeight != 0 && lastVisibleHeight > visibleHeight) {
                    // Keyboard is visible
                    chatRV.postDelayed({
                        chatRV.scrollToPosition(msgAdapter.itemCount - 1)
                    }, 100) // Add a slight delay to allow the keyboard to fully open
                }

                lastVisibleHeight = visibleHeight
            }
        })




        // Adding messages to RecyclerView from database
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    msgList.clear()

                    for (postSnapshot in snapshot.children){
                        val msg = postSnapshot.getValue(Message::class.java)
                        msgList.add(msg!!)
                    }

                    msgAdapter.notifyDataSetChanged()


                    // Scroll RecyclerView to the last item
                    chatRV.post {
                        chatRV.scrollToPosition(msgAdapter.itemCount - 1)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })




        // Adding messages to database
        btnSend.setOnClickListener{

            val msg = msgBox.text.toString()
            val msgObj = Message(msg, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(msgObj).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(msgObj)
                        .addOnSuccessListener {
                            // Scroll RecyclerView to the last item after sending the message
                            chatRV.post {
                                chatRV.scrollToPosition(msgAdapter.itemCount - 1)
                            }
                        }
                }

            msgBox.setText("")

        }


    }
}
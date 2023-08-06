package com.android.example.chatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var userRV: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Change the ActionBar color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))


        // Get the ProgressBar instance
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Set the ProgressBar color to black
        progressBar.indeterminateDrawable.setColorFilter(
            Color.BLACK, PorterDuff.Mode.SRC_IN
        )

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRV = findViewById(R.id.userRV)

        userRV.layoutManager = LinearLayoutManager(this)
        userRV.adapter = adapter



        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid == currentUser?.uid){
                        continue
                    }


                    userList.add(currentUser!!)

                }

                adapter.notifyDataSetChanged()


                // Hide the ProgressBar and show the RecyclerView
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                userRV.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide the ProgressBar in case of an error (optional)
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }

        })






    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout){
            // Logic for log out
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LogIn::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }



}
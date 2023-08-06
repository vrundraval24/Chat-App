package com.android.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val msgList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 1){
            // Inflate received
            val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
            return ReceivedViewHolder(view)
        }else{
            // Inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)

        }


    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentMsg = msgList[position]


        if (holder.javaClass == SentViewHolder::class.java){


            val viewHolder = holder as SentViewHolder

            holder.sentMsg.text = currentMsg.msg

        }else{


            val viewHolder = holder as ReceivedViewHolder

            holder.receivedMsg.text = currentMsg.msg

        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMsg = msgList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMsg.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVED
        }

    }



    class  SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMsg = itemView.findViewById<TextView>(R.id.txt_sent_msg)
    }

    class  ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receivedMsg = itemView.findViewById<TextView>(R.id.txt_received_msg)
    }
}
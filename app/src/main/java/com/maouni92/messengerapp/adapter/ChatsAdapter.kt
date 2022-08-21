package com.maouni92.messengerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.helper.getDateFormat
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(val context: Context, val chatsList: ArrayList<Message>, val listener : OnItemClickListener) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                   .inflate(R.layout.chats_recycler_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     val chatItem = chatsList[position]


     holder.name.text = chatItem.friendName
     holder.message.text = chatItem.message

     val date = chatItem.date?.getDateFormat(chatItem.time!!)
     holder.time.text = date

     Glide.with(context)
            .load(chatItem.friendImageUrl)
            .centerCrop()
            .placeholder(R.drawable.profile_picture)
            .into(holder.userImageView)


    }

    override fun getItemCount(): Int {
      return  chatsList.size
    }

    fun updateData(chats:ArrayList<Message>){
        chatsList.clear()
        chatsList.addAll(chats)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{



        init {
            itemView.setOnClickListener(this)
        }

        val name:TextView = itemView.findViewById(R.id.chats_name_tv)
        val message:TextView = itemView.findViewById(R.id.chats_message_tv)
        val time:TextView = itemView.findViewById(R.id.chats_time_tv)
        val userImageView:CircleImageView = itemView.findViewById(R.id.chat_user_image)



        override fun onClick(p0: View?) {
            listener.onItemClick(adapterPosition)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemPosition : Int)
    }


}
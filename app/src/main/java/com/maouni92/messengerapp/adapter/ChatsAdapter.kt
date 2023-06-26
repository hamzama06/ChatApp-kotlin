package com.maouni92.messengerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.helper.getDateFormat
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

// val chatsList: ArrayList<Message>,
class ChatsAdapter(val context: Context, val listener : OnItemClickListener) :
   ListAdapter<Message, ChatsAdapter.ViewHolder>(DiffCallback)
{


   companion object DiffCallback : DiffUtil.ItemCallback<Message>(){
       override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
          return oldItem.messageId == newItem.messageId
       }

       override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
          return oldItem == newItem
       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                   .inflate(R.layout.chats_recycler_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     val chatItem = getItem(position)


     holder.name.text = chatItem.friendName
     holder.message.text = chatItem.message

     val date = chatItem.date?.getDateFormat(chatItem.time!!)
     holder.time.text = date

     Glide.with(context)
            .load(chatItem.friendImageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .into(holder.userImageView)


    }

    inner class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{



        init {
            itemView.setOnClickListener(this)
        }
       // val userImageView = itemView.findViewById<ImageView>(R.id.chat_user_image)
        val name = itemView.findViewById<TextView>(R.id.chats_name_tv)
        val message = itemView.findViewById<TextView>(R.id.chats_message_tv)
        val time = itemView.findViewById<TextView>(R.id.chats_time_tv)
        val userImageView = itemView.findViewById<CircleImageView>(R.id.chat_user_image)



        override fun onClick(p0: View?) {
            listener.onItemClick(getItem(adapterPosition))
        }
    }

    interface OnItemClickListener{
        fun onItemClick(message : Message)
    }


}
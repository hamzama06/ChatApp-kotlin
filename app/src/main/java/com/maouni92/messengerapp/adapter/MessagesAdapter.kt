package com.maouni92.messengerapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.helper.getDateFormat
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.MessageType
import de.hdodenhof.circleimageview.CircleImageView

class MessagesAdapter(val context: Context, val messagesList:ArrayList<Message>, val listener: OnItemLongClickListener) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

     lateinit var currentMessage: Message
     lateinit var holder:ViewHolder



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.messages_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        this.currentMessage = messagesList[position]
        this.holder = holder

        if (currentMessage.senderId == currentMessage.userId){

            if (currentMessage.type == MessageType.TEXT){
               buildMessage( myMessageVisibility = View.VISIBLE,
                   messageTextView = holder.myMessage, messageTimeTextView = holder.myTime)
            }else{

                buildMessage(myMessageImageVisibility = View.VISIBLE, messageImageView = holder.myImageMessage,
                             messageTimeTextView = holder.myImageMessageTime )}

          }else{

            if (currentMessage.type == MessageType.TEXT){
        buildMessage(recMessageVisibility = View.VISIBLE,
                    messageTextView = holder.recMessage,
                    friendProfileImageView = holder.recProfileImage,
                    messageTimeTextView =  holder.recTime
                    )
            }else{
                buildMessage(recMessageImageVisibility = View.VISIBLE,
                             messageImageView = holder.recMessageImage,
                             friendProfileImageView = holder.recImgProfileImage,
                             messageTimeTextView = holder.recImgMessageTime ) } }
           }

    override fun getItemCount(): Int {
       return messagesList.size
    }

    fun add( message:Message){
        messagesList.add(message)
        notifyDataSetChanged()

    }

    fun updateData(messages:ArrayList<Message>){
        messagesList.clear()
        messagesList.addAll(messages)
        notifyDataSetChanged()
    }

    fun removeAt(itemPosition:Int){
        messagesList.removeAt(itemPosition)
        notifyItemRemoved(itemPosition)
    }

   inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnLongClickListener {



       init {
           itemView.setOnLongClickListener(this)
       }


       var myMessage: TextView = itemView.findViewById(R.id.message_body_tv)
       var myTime: TextView = itemView.findViewById(R.id.message_time_tv)
       var recMessage: TextView = itemView.findViewById(R.id.rec_message_body_tv)
       var recTime: TextView = itemView.findViewById(R.id.rec_message_time_tv)
       var recProfileImage: CircleImageView = itemView.findViewById(R.id.rec_message_user_image)
       var myMessageLayout: RelativeLayout = itemView.findViewById(R.id.my_message_layout)
       var recMessageLayout: RelativeLayout = itemView.findViewById(R.id.rec_message_layout)
       var myImageMsgLayout: RelativeLayout = itemView.findViewById(R.id.my_message_image_layout)
       var recImageMsgLayout: RelativeLayout = itemView.findViewById(R.id.rec_message_image_layout)
       var myImageMessage: ImageView = itemView.findViewById(R.id.message_image)
       var myImageMessageTime: TextView = itemView.findViewById(R.id.message_image_time_tv)
       var recMessageImage: ImageView = itemView.findViewById(R.id.rec_message_image)
       var recImgProfileImage: CircleImageView =
           itemView.findViewById(R.id.rec_message_img_user_image)
       var recImgMessageTime: TextView = itemView.findViewById(R.id.rec_message_image_time_tv)


       override fun onLongClick(p0: View?): Boolean {
          return listener.onItemLongClick(adapterPosition)
       }

    }


  private fun buildMessage(
                           myMessageVisibility:Int = View.GONE,
                           recMessageVisibility:Int = View.GONE,
                           myMessageImageVisibility: Int = View.GONE,
                           recMessageImageVisibility:Int = View.GONE,
                           messageTextView:TextView? = null,
                           messageImageView: ImageView? = null,
                           friendProfileImageView: CircleImageView? = null,
                           messageTimeTextView:TextView){

      holder.myMessageLayout.visibility = myMessageVisibility
      holder.recMessageLayout.visibility = recMessageVisibility
      holder.myImageMsgLayout.visibility = myMessageImageVisibility
      holder.recImageMsgLayout.visibility = recMessageImageVisibility

      if (messageTextView != null) messageTextView.text = currentMessage.message
      if (messageImageView != null){
          Glide.with(context)
              .load(currentMessage.message)
              .centerCrop()
              .placeholder(R.drawable.profile_picture)
              .into(messageImageView)
      }
      if (friendProfileImageView != null){
              Glide.with(context)
                  .load(currentMessage.friendImageUrl)
                  .centerCrop()
                  .placeholder(R.drawable.profile_picture)
                  .into(friendProfileImageView)

      }
      val date = currentMessage.date?.getDateFormat(currentMessage.time!!)

      messageTimeTextView.text = date
  }

    interface OnItemLongClickListener{
        fun onItemLongClick(itemPosition : Int) : Boolean
    }

}
package com.maouni92.messengerapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.databinding.MessagesRecyclerItemBinding
import com.maouni92.messengerapp.helper.getDateFormat
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.MessageType
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val listener: OnItemLongClickListener)
    :
 ListAdapter <Message, MessageAdapter.ViewHolder>(DiffCallback)
{
   lateinit var currentMessage: Message

   companion object DiffCallback: DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return   oldItem == newItem
        }
   }
    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {

        currentMessage = getItem(position)!!
        currentMessage.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MessagesRecyclerItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: MessagesRecyclerItemBinding): RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {

        init {
            itemView.setOnLongClickListener(this)
        }

        fun bind(currentMessage:Message){
            if (currentMessage.senderId == currentMessage.userId){

                if (currentMessage.type == MessageType.TEXT){
                    buildMessage( myMessageVisibility = View.VISIBLE,
                        messageTextView = binding.messageBodyTv, messageTimeTextView = binding.messageTimeTv)
                }else{

                    buildMessage(myMessageImageVisibility = View.VISIBLE, messageImageView = binding.messageImage,
                        messageTimeTextView = binding.messageImageTimeTv )}

            }else{

                if (currentMessage.type == MessageType.TEXT){
                    buildMessage(recMessageVisibility = View.VISIBLE,
                        messageTextView = binding.recMessageBodyTv,
                        friendProfileImageView = binding.recMessageUserImage,
                        messageTimeTextView =  binding.recMessageTimeTv
                    )
                }else{
                    buildMessage(recMessageImageVisibility = View.VISIBLE,
                        messageImageView = binding.recMessageImage,
                        friendProfileImageView = binding.recMessageImgUserImage,
                        messageTimeTextView =  binding.recMessageImageTimeTv ) } }
        }

        override fun onLongClick(p0: View?): Boolean {
            return listener.onItemLongClick(adapterPosition)
        }

        private fun buildMessage(
            myMessageVisibility:Int = View.GONE,
            recMessageVisibility:Int = View.GONE,
            myMessageImageVisibility: Int = View.GONE,
            recMessageImageVisibility:Int = View.GONE,
            messageTextView: TextView? = null,
            messageImageView: ImageView? = null,
            friendProfileImageView: CircleImageView? = null,
            messageTimeTextView: TextView
        ){

            binding.myMessageLayout.visibility = myMessageVisibility
            binding.recMessageLayout.visibility = recMessageVisibility
            binding.myMessageImageLayout.visibility = myMessageImageVisibility
            binding.recMessageImageLayout.visibility = recMessageImageVisibility

            if (messageTextView != null) messageTextView.text = currentMessage.message

            if (messageImageView != null){

                Glide.with(context)
                    .load(currentMessage.message)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)// Cache image
                    .centerCrop()
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_person)) // Optional placeholder
                    .into(messageImageView)
            }


            if (friendProfileImageView != null){


                Glide.with(context)
                    .load(currentMessage.friendImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()// Cache image
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_person)) // Optional placeholder
                    .into(friendProfileImageView)
            }
            val date = currentMessage.date?.getDateFormat(currentMessage.time!!)
            messageTimeTextView.text = date
        }
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(itemPosition : Int) : Boolean
    }
}
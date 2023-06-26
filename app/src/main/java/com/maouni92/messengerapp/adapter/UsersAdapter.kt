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
import com.maouni92.messengerapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(val context: Context, val listener: OnItemClickListener) :
    ListAdapter<User, UsersAdapter.ViewHolder>(DiffCallback) {


  companion object DiffCallback: DiffUtil.ItemCallback<User>(){
      override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
          return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
         return oldItem == newItem
      }
  }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val user = getItem(position)

      holder.name.text = user.name
      Glide.with(context)
            .load(user.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_person)
            .into(holder.userImage)
    }


   inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


        init {
             itemView.setOnClickListener(this)
        }

        var name: TextView = itemView.findViewById(R.id.people_name_tv)
        var userImage = itemView.findViewById<CircleImageView>(R.id.people_user_image)

        override fun onClick(p0: View?) {
           listener.onItemClick(getItem(adapterPosition))
        }
    }

    interface OnItemClickListener{
        fun onItemClick(user : User)
    }
}
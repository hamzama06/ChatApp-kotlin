package com.maouni92.messengerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class PeopleAdapter(val context: Context, var usersList: ArrayList<User>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.people_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val user = usersList[position]

      holder.name.text = user.name
        Glide.with(context)
            .load(user.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.messenger_logo)
            .into(holder.userImage)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun updateData(users:ArrayList<User>){
        usersList.clear()
        usersList.addAll(users)
        notifyDataSetChanged()
    }

   inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


        init {
             itemView.setOnClickListener(this)
        }

        var name: TextView = itemView.findViewById(R.id.people_name_tv)
        var userImage:CircleImageView = itemView.findViewById(R.id.people_user_image)

        override fun onClick(p0: View?) {
           listener.onItemClick(adapterPosition)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemPosition : Int)
    }
}
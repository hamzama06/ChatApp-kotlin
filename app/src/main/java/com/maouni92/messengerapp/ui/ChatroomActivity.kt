package com.maouni92.messengerapp.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maouni92.messengerapp.BaseActivity
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.adapter.MessageAdapter
import com.maouni92.messengerapp.databinding.ActivityChatroomBinding
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.initStatusBar
import com.maouni92.messengerapp.model.MessageType
import com.maouni92.messengerapp.viewModel.ChatroomViewModel

class ChatroomActivity : BaseActivity(), MessageAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityChatroomBinding

    private val chatroomViewModel: ChatroomViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private var userId = ""
    private var friendId = ""
    private var friendImageUrl = ""
    private var friendName = ""
    private var userName: String? = ""
    private var userImageUrl = ""
    private var friendToken: String? = null
    private lateinit var messagesAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize status bar
        initStatusBar()

        setSupportActionBar(binding.chatroomToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        messagesAdapter = MessageAdapter(this, this)

        initData()
        initRecyclerView()

        chatroomViewModel.currentUser.observe(this) { user ->
            userId = user.id!!
            userName = user.name
            userImageUrl = user.imageUrl!!
        }

        chatroomViewModel.isFriendAvailable.observe(this) { isAvailable ->
            updateAvailabilityText(
                isAvailable
            )
        }
        chatroomViewModel.friendToken.observe(this) { token -> friendToken = token }


       chatroomViewModel.getAllMessages(friendId, recyclerView)

            chatroomViewModel.messages.observe(this){messages->
               messagesAdapter.submitList(messages)
                recyclerView.adapter = messagesAdapter
            }

        binding.sendMessageButton.setOnClickListener {
            val message = binding.messageField.text.toString()

            if(message.isEmpty()) return@setOnClickListener
            chatroomViewModel.sendNewMessage(message, userName!!, userImageUrl,friendId, friendName, friendImageUrl, MessageType.TEXT, recyclerView)
            binding.messageField.setText("")
        }

        binding.chatroomImagePicker.setOnClickListener {
            imageLauncher.launch("image/*")
        }  }

    private fun initRecyclerView(){
        recyclerView = binding.chatroomRecyclerViw
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

    }

    private fun initData(){

        if (intent != null){

            friendId = intent.getStringExtra(Constants.FRIEND_ID_KEY)!!
            friendName = intent.getStringExtra(Constants.FRIEND_NAME_KEY)!!
            friendImageUrl = intent.getStringExtra(Constants.FRIEND_IMAGE_KEY)!!

            binding.toolbarNameProfile.text = friendName
            Glide.with(this)
                .load(friendImageUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_picture)
                .into(binding.toolbarImageProfile)
        }
    }

    private fun updateAvailabilityText(isAvailable:Boolean){
        binding.toolbarFriendAvailability.apply {
            text = if(isAvailable) context.getString(R.string.online) else context.getString(R.string.offline)
            setTextColor(
                if (isAvailable) Color.GREEN else Color.RED
            )
        }
    }

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        chatroomViewModel.sendImage(uri,userName!!, userImageUrl,friendId, friendName, friendImageUrl,recyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        chatroomViewModel.cancelMessagesRegistration()
        chatroomViewModel.cancelFriendChangesRegistration()
    }

    override fun onItemLongClick(itemPosition: Int): Boolean {
        deleteMessage(itemPosition)
        return true
    }

    private fun deleteMessage(itemPosition: Int){
        val builder = AlertDialog.Builder(this, R.style.myAlertDialog)

        builder.setTitle(getString(R.string.delete_message_dialog_title))
        builder.setMessage(getString(R.string.delete_message_dialog_permission))


        builder.setPositiveButton(getString(R.string.yes)){ dialog, _ ->
            chatroomViewModel.deleteMessage(itemPosition)
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)){ dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        chatroomViewModel.listenReceiverChanges(friendId)
    }
}
package com.maouni92.messengerapp.ui.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.adapter.ChatsAdapter
import com.maouni92.messengerapp.databinding.FragmentChatsBinding
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.getDateFormat
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.ui.ChatroomActivity
import com.maouni92.messengerapp.ui.SearchActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatsFragment : Fragment(), ChatsAdapter.OnItemClickListener{

    private var _binding: FragmentChatsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val chatsViewModel:ChatsViewModel by viewModels()
    private val chatsList = ArrayList<Message>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        recyclerView = binding.chatsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)

        binding.searchEt.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

      adapter = ChatsAdapter(requireContext(), this)

        chatsViewModel.getAllChats()

        chatsViewModel.chats.observe(viewLifecycleOwner) { chats ->
            adapter.submitList(chats)
            recyclerView.adapter = adapter

        }
        return root
    }

    override fun onItemClick(message: Message) {

        val intent = Intent(this.context, ChatroomActivity::class.java)
       // intent.putExtra(Constants.USER_ID_KEY, userId)
        intent.putExtra(Constants.FRIEND_ID_KEY, message.friendId)
        intent.putExtra(Constants.FRIEND_NAME_KEY, message.friendName)
        intent.putExtra(Constants.FRIEND_IMAGE_KEY, message.friendImageUrl)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        chatsViewModel.cancelChatsRegistration()
    }

    override fun onDestroy() {
        super.onDestroy()
        chatsViewModel.cancelChatsRegistration()

    }
}
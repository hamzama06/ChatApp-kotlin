package com.maouni92.messengerapp.ui.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maouni92.messengerapp.BuildConfig
import com.maouni92.messengerapp.adapter.ChatsAdapter
import com.maouni92.messengerapp.databinding.FragmentChatsBinding
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.ui.ChatroomActivity
import com.maouni92.messengerapp.ui.SearchActivity
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

      adapter = ChatsAdapter(requireContext(), chatsList, this)
        recyclerView.adapter = adapter

        chatsViewModel.getAllChats()

        chatsViewModel.chats.observe(viewLifecycleOwner) { chats ->
            adapter.updateData(chats)

        }
        return root
    }

    override fun onItemClick(itemPosition: Int) {

        val chat = chatsList[itemPosition]

        val intent = Intent(this.context, ChatroomActivity::class.java)
        intent.putExtra(Constants.FRIEND_ID_KEY, chat.friendId)
        intent.putExtra(Constants.FRIEND_NAME_KEY, chat.friendName)
        intent.putExtra(Constants.FRIEND_IMAGE_KEY, chat.friendImageUrl)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onDestroy() {
        super.onDestroy()
        chatsViewModel.cancelChatsRegistration()

    }
}
package com.maouni92.messengerapp.ui.users

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maouni92.messengerapp.adapter.UsersAdapter
import com.maouni92.messengerapp.databinding.FragmentPeopleBinding
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.ui.ChatroomActivity

class UsersFragment : Fragment(), UsersAdapter.OnItemClickListener, FirebaseListener {


    private var _binding: FragmentPeopleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val peopleViewModel:UsersViewModel by viewModels()

    private val usersList = ArrayList<User>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: UsersAdapter

    override fun onStart() {
        super.onStart()
        //getUsers()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.peopleProgressBar
        progressBar.visibility = View.VISIBLE
        recyclerView = binding.peopleRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter =  UsersAdapter(requireContext(), this)
        recyclerView.adapter = adapter


        peopleViewModel.getUsers(this)
        peopleViewModel.users.observe(viewLifecycleOwner, Observer { users->
            adapter.submitList(users)
            recyclerView.adapter = adapter
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCompleteListener() {
        progressBar.visibility = View.GONE
    }

    override fun onFailureListener() {
        progressBar.visibility = View.GONE
    }

    override fun onItemClick(user: User) {
        val intent = Intent(context, ChatroomActivity::class.java)
        intent.putExtra(Constants.FRIEND_ID_KEY, user.id)
        intent.putExtra(Constants.FRIEND_IMAGE_KEY, user.imageUrl)
        intent.putExtra(Constants.FRIEND_NAME_KEY, user.name)
        startActivity(intent)
    }
}
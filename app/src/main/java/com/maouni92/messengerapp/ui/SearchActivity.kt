package com.maouni92.messengerapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maouni92.messengerapp.BaseActivity
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.adapter.PeopleAdapter
import com.maouni92.messengerapp.databinding.ActivitySearchBinding
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.initStatusBar
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.viewModel.SearchViewModel

class SearchActivity : BaseActivity(), PeopleAdapter.OnItemClickListener {

    lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()

    private var usersList = ArrayList<User>()
    private var filteredList = ArrayList<User>()
    lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var usersAdapter: PeopleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchToolbar)

        initStatusBar()

        supportActionBar?.let {
            it.title = ""
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)

        }

        progressBar = binding.searchProgressBar

        recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersAdapter = PeopleAdapter(this, filteredList, this)
        recyclerView.adapter = usersAdapter

        //initData()
        searchViewModel.getAllUser(null)

        searchViewModel.filteredUsers.observe(this) { filteredUsers ->
            filteredList.clear()
            filteredList.addAll(filteredUsers)
            usersAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu?.findItem(R.id.action_search)

        searchMenuItem?.expandActionView()
        ((searchMenuItem)?.actionView as SearchView).apply {
            isIconifiedByDefault = false

            isIconified = false
            queryHint = context.getString(R.string.search_hint)
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    searchViewModel.searchUsers(query!!)
                    return true
                }})
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return false
    }


    override fun onItemClick(itemPosition: Int) {
        val user = usersList[itemPosition]

        val intent = Intent(this, ChatroomActivity::class.java)
        intent.putExtra(Constants.FRIEND_ID_KEY, user.id)
        intent.putExtra(Constants.FRIEND_IMAGE_KEY, user.imageUrl)
        intent.putExtra(Constants.FRIEND_NAME_KEY, user.name)
        startActivity(intent)
    }


}
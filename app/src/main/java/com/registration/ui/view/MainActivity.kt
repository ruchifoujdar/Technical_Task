package com.registration.ui.view

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.registration.ui.intent.MainIntent
import com.registration.data.api.ApiHelperImpl
import com.registration.data.api.RetrofitBuilder
import com.registration.ui.adapter.CustomAdapter
import com.registration.ui.viewmodel.MainViewModel
import com.registration.ui.viewstate.MainState
import com.registration.R
import com.registration.data.api.ApiHelperImpl.Companion.ACCESS_TOKEN
import com.registration.data.model.User
import com.registration.util.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.text.TextUtils
import android.util.Patterns


class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private var adapter = CustomAdapter(arrayListOf())
    private var list: List<User.Details>? = null
    private var email_str = ""
    private var name_str = ""
    private var id_str = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting ui
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.run {
            addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        recyclerView.adapter = adapter
        if (isConnectedInternet()) {
            button.visibility = View.INVISIBLE
        } else {
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                if (isConnectedInternet()) {
                    setupViewModel()
                    observeViewModel()
                    button.visibility = View.INVISIBLE
                }
            }
        }
        adapter.setOnItemClickListener(object : CustomAdapter.ClickListener {
            override fun onItemClick(v: View, position: Int) {
                list?.get(position)?.email
                list?.get(position)?.name
                list?.get(position)?.id
                showCustomAlert("delete")
            }
        })

        setupViewModel()
        observeViewModel()
    }

    private fun setupViewModel() {
        //setting up viewModel
        mainViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(
                    RetrofitBuilder.apiService
                )
            )
        ).get(MainViewModel::class.java)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.state.collect {
                when (it) {
                    is MainState.Idle -> {
                    }
                    is MainState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is MainState.Users -> {
                        progressBar.visibility = View.GONE
                        renderList(it.user)
                    }
                    is MainState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                    }
                    is MainState.Success -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, it.success, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            mainViewModel.userIntent.send(MainIntent.FetchUser)
        }

    }

    private fun renderList(users: List<User>) {
        recyclerView.visibility = View.VISIBLE
        users.let { listOfUsers -> listOfUsers.let { adapter.addData(it) } }
        adapter.notifyDataSetChanged()
        list = users[0].data
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            showCustomAlert("add");
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showCustomAlert(action: String) {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        val confirmBtn = dialogView.findViewById<Button>(R.id.confirm_bt)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_bt)
        val email = dialogView.findViewById<EditText>(R.id.email_et)
        val name = dialogView.findViewById<EditText>(R.id.name_et)
        val textDelete = dialogView.findViewById<TextView>(R.id.tv_delete)
        cancelBtn.setOnClickListener {
            customDialog.dismiss()
        }
        if (action == "delete") {
            name.visibility = View.GONE
            textDelete.visibility = View.VISIBLE
            email.visibility = View.GONE
            confirmBtn.setOnClickListener {
                if (isConnectedInternet()) {
                    lifecycleScope.launch {
                        mainViewModel.sendData(
                            ACCESS_TOKEN,
                            id_str!!, name_str!!, email_str!!
                        )
                        mainViewModel.userIntent.send(MainIntent.DeleteUser)
                    }
                    customDialog.dismiss()
                }

            }
        } else if (action == "add") {
            name.visibility = View.VISIBLE
            textDelete.visibility = View.GONE
            email.visibility = View.VISIBLE

            confirmBtn.setOnClickListener {
                if (isConnectedInternet()) {
                    if (email.text.toString() == ""
                        || email.text.toString() == " "
                        || email.text.toString() == null
                        || !(isValidEmail(email.text.toString()))
                    ) {

                        Toast.makeText(
                            this@MainActivity,
                            "Email validation failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if ((name.text.toString() == ""
                                || name.text.toString() == " "
                                || name.text.toString() == null
                                || name.text.toString().length <= 3)
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            "Name validation failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lifecycleScope.launch {
                            mainViewModel.sendData(
                                ACCESS_TOKEN,
                                0, name.text.toString(), email.text.toString()
                            )
                            mainViewModel.userIntent.send(MainIntent.AddUser)
                        }
                        customDialog.dismiss()
                    }
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isConnectedInternet(): Boolean {
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo()
                .isConnected()
        ) {
            Toast.makeText(
                this@MainActivity,
                "Internet Available!",
                Toast.LENGTH_SHORT
            ).show()
            return true
        } else {
            Toast.makeText(
                this@MainActivity,
                "No Internet!",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }
}

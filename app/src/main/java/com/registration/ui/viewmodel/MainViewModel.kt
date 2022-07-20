package com.registration.ui.viewmodel

import MainRepository
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.registration.R
import com.registration.data.model.User
import com.registration.ui.intent.MainIntent
import com.registration.ui.viewstate.MainState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState>
        get() = _state
    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
        }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    init {
        handleIntent()
    }
    private var accessToken_:String = ""
    private var name_:String = ""
    private var email_:String = ""
    private var id_:Int = 0

    private fun handleIntent() {

        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.FetchUser -> fetchUser()
                    is MainIntent.AddUser -> addUser(accessToken_,name_,email_,"female","active")
                    is MainIntent.DeleteUser -> deleteUser(accessToken_,id_)
                }
            }
        }
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            _state.value = try {
                MainState.Users(repository.getUsers())
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
        }
    }

    private fun addUser(
        accessToken: String,
        name:String, email:String, gender:String, status:String) {
        viewModelScope.launch {
            _state.value = MainState.Loading
            _state.value = try {
                MainState.AddUser(repository.addUser(accessToken,name,email,gender,status))
                MainState.Success("User data added")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
        }
    }

    fun sendData(accessToken: String,id:Int,
            name:String, email:String) {
        accessToken_ = accessToken
        name_ = name
        email_ = email
        id_ = id
    }

    private fun deleteUser(
        accessToken: String,
        id:Int) {
        viewModelScope.launch {
            _state.value = MainState.Loading
            _state.value = try {
                MainState.DeleteUser(repository.deleteUser(accessToken,id))
                MainState.Success("User deleted")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }

        }
    }
}











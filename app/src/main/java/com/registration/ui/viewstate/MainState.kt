package com.registration.ui.viewstate

import com.registration.data.model.DataUser
import com.registration.data.model.User

sealed class MainState {

    object Idle : MainState()
    object Loading : MainState()
    data class Users(val user: List<User>) : MainState()
    data class AddUser(val user: DataUser?) : MainState()
    data class DeleteUser(val user: String?) : MainState()
    data class Error(val error: String?) : MainState()
    data class Success(val success: String?) : MainState()
}
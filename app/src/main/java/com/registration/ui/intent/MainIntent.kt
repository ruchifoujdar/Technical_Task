package com.registration.ui.intent

import android.widget.EditText
import com.registration.data.model.DataUser

sealed class MainIntent {

    object FetchUser : MainIntent()
    object AddUser : MainIntent()
    object DeleteUser : MainIntent()

}
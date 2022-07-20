package com.registration.data.api

import com.registration.data.model.DataUser
import com.registration.data.model.User

interface ApiHelper {

    suspend fun getUsers(): List<User>
    suspend fun addUser(accessToken:String,
                        name:String,email:String,gender:String,status:String): DataUser
    suspend fun deleteUser(accessToken:String, id:Int): String
}
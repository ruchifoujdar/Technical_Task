package com.registration.data.api

import com.registration.data.model.DataUser
import com.registration.data.model.User

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    companion object {
        var ACCESS_TOKEN:String="fdb446c71eeaf3c11723c5edfa5956971b88581754f1485fa577fbc9fb4e246e";
    }

    override suspend fun getUsers(): List<User> {
        return listOf(apiService.getUsers())
    }

    override suspend fun addUser(accessToken:String,
    name:String,email:String,gender:String,status:String): DataUser {
        return apiService.addUser(accessToken,
            name,email,gender,status)
    }

    override suspend fun deleteUser(accessToken: String, id: Int):String {
       return apiService.deleteUser(accessToken,id)
    }
}
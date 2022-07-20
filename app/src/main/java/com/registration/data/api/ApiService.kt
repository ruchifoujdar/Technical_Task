package com.registration.data.api

import com.registration.data.model.DataUser
import com.registration.data.model.User
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

   @GET("users")
   suspend fun getUsers(): User

   @POST("users?")
   suspend fun addUser(@Query("access-token") access_token: String,
                       @Query("name") name: String,
                       @Query("email") email: String,
                       @Query("gender") gender: String,
                       @Query("status") status: String): DataUser

    @DELETE("users?")
    suspend fun deleteUser(@Query("access-token") access_token: String,
                        @Query("id") name: Int):String
}
package com.registration.data.model

import com.squareup.moshi.Json

data class User (
    @Json(name = "data")
    var data: List<Details>? = null
){
    data class Details (
        @Json(name = "id")
        val id: Int? = null,

        @Json(name = "name")
        val name: String? = null,

        @Json(name = "email")
        var email: String? = null
    )
}

data class DataUser (
    @Json(name = "data")
    var data: User.Details? = null
)

package com.example.watermeterapp.data

data class LoginResponse(
        val id: Int,
        val firstName: String,
        val lastName: String,
        val username: String,
        val createdAt: String,
        val updatedAt: String,
        val token:String
)
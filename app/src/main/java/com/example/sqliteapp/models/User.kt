package com.example.sqliteapp.models

data class User(
    val id: Int? = null,
    val name: String = "",
    val lastname: String = "",
    val age: Int? = null,
    val gender: String = "",
    val phone: String = "",
    val email: String = ""
)
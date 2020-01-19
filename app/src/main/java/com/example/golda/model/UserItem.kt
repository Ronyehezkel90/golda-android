package com.example.golda.model

import org.bson.types.ObjectId

data class UserItem constructor(
    val _id: ObjectId,
    val name: String,
    val password: String,
    val role: String,
    var selected: Boolean = false
)
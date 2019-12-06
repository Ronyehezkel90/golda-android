package com.example.golda.model

import org.bson.types.ObjectId

data class BranchItem(
    val _id: ObjectId,
    val managerId: ObjectId,
    val name: String,
    val address: String,
    var phone: String
)
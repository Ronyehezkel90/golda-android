package com.example.golda.model

import org.bson.types.ObjectId
import java.util.*

data class ReviewRelationItem(
    val _id: ObjectId,
    val branchId: ObjectId,
    val date: String,
    val reviewerId: ObjectId,
    var reviewResultId: ObjectId
)
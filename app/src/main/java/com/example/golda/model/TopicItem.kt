package com.example.golda.model

import org.bson.types.ObjectId

data class TopicItem(val _id: ObjectId, val topic: String, var selected:Boolean = false)
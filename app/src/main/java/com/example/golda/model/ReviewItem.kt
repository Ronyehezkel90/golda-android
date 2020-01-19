package com.example.golda.model

import android.graphics.Bitmap
import org.bson.types.ObjectId

data class ReviewItem(
    val _id: ObjectId,
    val title: String,
    val subtitle: String,
    val topic: ObjectId,
    var imageBitmap: Bitmap? = null,
    var imageUrl: String,
    var rank: Int = -1,
    var comment: String,
    var selected: Boolean = false
)
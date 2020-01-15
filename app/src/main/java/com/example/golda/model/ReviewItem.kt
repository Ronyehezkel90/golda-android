package com.example.golda.model

import android.graphics.Bitmap
import com.jakewharton.rxrelay2.BehaviorRelay
import org.bson.types.ObjectId

data class ReviewItem(
    val _id: ObjectId,
    val title: String,
    val subtitle: String,
    val topic: Int,
    var imageBitmap: Bitmap? = null,
    var imageUrl: String,
    var rank: Int = -1,
    var comment: String
)
package com.example.golda.reviews

import android.graphics.Bitmap

data class ReviewItem(
    val title: String,
    val subtitle: String,
    val topic: Int,
    var image: Bitmap? = null
)
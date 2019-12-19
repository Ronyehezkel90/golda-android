package com.example.golda

import androidx.lifecycle.MutableLiveData
import com.example.golda.model.ReviewItem
import com.google.gson.Gson
import javax.inject.Inject


class Repository @Inject constructor(
    val mongoManager: MongoManager,
    val gson: Gson
) {
    private val reviewItemsLiveData = MutableLiveData<List<ReviewItem>>()

    fun getReviews(): MutableLiveData<List<ReviewItem>> {
        mongoManager.getReviews().addOnSuccessListener {
            val reviewsList = mutableListOf<ReviewItem>()
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                reviewsList.add(reviewItem)
            }
            reviewItemsLiveData.setValue(reviewsList)
        }
        return reviewItemsLiveData
    }
}

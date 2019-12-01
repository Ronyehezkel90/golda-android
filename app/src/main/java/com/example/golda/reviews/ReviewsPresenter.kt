package com.example.golda.reviews

import com.example.golda.MongoManager
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import javax.inject.Inject

class ReviewsPresenter
@Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson

) : MvpNullObjectBasePresenter<ReviewsView>() {

    val topicReviewsMap = mutableMapOf<Int, MutableList<ReviewItem>>()

    override fun attachView(view: ReviewsView?) {
        super.attachView(view)
        displayItems()
    }

    private fun displayItems() {
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                if (reviewItem.topic !in topicReviewsMap) {
                    topicReviewsMap[reviewItem.topic] = ArrayList()
                }
                topicReviewsMap[reviewItem.topic]!!.add(reviewItem)
            }
            view.createAdapter()
        }
    }
}

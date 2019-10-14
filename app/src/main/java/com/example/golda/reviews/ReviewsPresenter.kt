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

    override fun attachView(view: ReviewsView?) {
        super.attachView(view)
        displayItems()
    }

    fun displayItems() {
        val reviewItemsList = mutableListOf<ReviewItem>()
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                reviewItemsList.add(gson.fromJson(it.toJson(), ReviewItem::class.java))
            }
            view.showItems(reviewItemsList)
        }
    }
}

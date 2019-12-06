package com.example.golda.reviews

import android.content.SharedPreferences
import com.example.golda.MongoManager
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import org.bson.types.ObjectId
import timber.log.Timber
import javax.inject.Inject

class ReviewsPresenter
@Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences

) : MvpNullObjectBasePresenter<ReviewsView>() {

    val topicReviewsMap = mutableMapOf<Int, MutableList<ReviewItem>>()

    override fun attachView(view: ReviewsView?) {
        super.attachView(view)
        displayReviews()
    }

    private fun displayReviews() {
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                if (reviewItem.topic !in topicReviewsMap) {
                    topicReviewsMap[reviewItem.topic] = ArrayList()
                }
                topicReviewsMap[reviewItem.topic]!!.add(reviewItem)
            }
            displayTopics()
        }
    }

    fun updateReviewItemRank(reviewId: ObjectId, rank: Int) {
        for (topic in topicReviewsMap) {
            for (review: ReviewItem in topic.value) {
                if (review._id == reviewId) {
                    review.rank = rank
                }
            }
        }
    }

    private fun displayTopics() {
        val topicItemsList = mutableListOf<TopicItem>()
        mongoManager.getTopics().addOnSuccessListener {
            it.forEach {
                topicItemsList.add(gson.fromJson(it.toJson(), TopicItem::class.java))
            }
            topicItemsList.sortBy { it.id }
            view.createAdapter(topicItemsList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun sendReview(branchId: ObjectId) {
        val userId = ObjectId(sharedPreferences.getString("userId", "no user id"))
        mongoManager.sendReview(topicReviewsMap, branchId, userId)
    }
}

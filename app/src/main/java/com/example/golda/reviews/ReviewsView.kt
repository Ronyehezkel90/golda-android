package com.example.golda.reviews

import android.graphics.Bitmap
import com.example.golda.model.TopicItem
import com.hannesdorfmann.mosby.mvp.MvpView

interface ReviewsView : MvpView {

    fun createAdapter(topicItemsList: MutableList<TopicItem>)

    fun goToTopic(topicId: Int)

    fun showTopics(topicItemsList: MutableList<TopicItem>)

    fun setLoaderVisibility(showLoader: Boolean)

    fun addComment(reviewFragment: ReviewFragment, reviewPosition: Int)

    fun getFragmentAdapter(reviewFragment: ReviewFragment): ReviewsAdapter

    fun closeActivity()

    fun downloadImageByKey(imageKey: String, reviewFragment: ReviewFragment)

    fun setImageByKey(
        itemPosition: Int,
        reviewFragment: ReviewFragment,
        bitmap: Bitmap
    )

    fun updateItems(topicIdx: Int)
}
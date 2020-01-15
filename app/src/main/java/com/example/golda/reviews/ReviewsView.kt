package com.example.golda.reviews

import com.example.golda.model.ReviewItem
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

    fun downloadImageByKey(imageKey: ReviewItem, reviewFragment: ReviewFragment)

    fun updateItems(topicIdx: Int)

    fun setSpinnerVisibility(isVisible: Boolean)
}
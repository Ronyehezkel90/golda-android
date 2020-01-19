package com.example.golda.reviews

import com.example.golda.model.TopicItem
import com.hannesdorfmann.mosby.mvp.MvpView
import org.bson.types.ObjectId

interface ReviewsView : MvpView {

    fun createAdapter(topicItemsList: MutableList<TopicItem>)

    fun goToTopic(pos: Int)

    fun showTopics(topicItemsList: MutableList<TopicItem>)

    fun addComment(reviewFragment: ReviewFragment, reviewPosition: Int, topicId:ObjectId)
}
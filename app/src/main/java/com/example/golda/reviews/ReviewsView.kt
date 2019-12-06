package com.example.golda.reviews

import com.example.golda.model.TopicItem
import com.hannesdorfmann.mosby.mvp.MvpView

interface ReviewsView : MvpView {

    fun createAdapter(topicItemsList: MutableList<TopicItem>)

    fun goToTopic(topicId: Int)

    fun showTopics(topicItemsList: MutableList<TopicItem>)
}
package com.example.golda.topics

import com.example.golda.MongoManager
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import timber.log.Timber
import javax.inject.Inject

class TopicsPresenter
@Inject constructor(
    val mongoManager: MongoManager,
    val gson: Gson
) :
    MvpNullObjectBasePresenter<MvpView>() {

    override fun attachView(view: MvpView?) {
        super.attachView(view)
        displayItems()
    }

    private fun displayItems() {
        val topicItemsList = mutableListOf<TopicItem>()
        mongoManager.getTopics().addOnSuccessListener {
            it.forEach {
                topicItemsList.add(gson.fromJson(it.toJson(), TopicItem::class.java))
            }
            topicItemsList.sortBy { it.id }
            (view as TopicsActivity).showItems(topicItemsList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }
}

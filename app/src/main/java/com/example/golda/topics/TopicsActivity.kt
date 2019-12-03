package com.example.golda.topics

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.dagger.App
import com.example.golda.reviews.ReviewsActivity
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.hannesdorfmann.mosby.mvp.MvpView
import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : MvpActivity<MvpView, TopicsPresenter>() {
    lateinit var topicsAdapter: TopicsAdapter
    val topicItemsList = ArrayList<String>()

    companion object {
        val TOPIC_ID_EXTRA = "TOPIC_ID_EXTRA"
        val TOPIC_NAMES_EXTRA = "TOPIC_NAMES_EXTRA"
    }

    override fun createPresenter(): TopicsPresenter {
        return App.getAppComponent(this).getTopicsComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        topicsAdapter = TopicsAdapter { id -> openReviewsActivityWithId(id) }
        topicsRecyclerView.layoutManager = LinearLayoutManager(this)
        topicsRecyclerView.adapter = this.topicsAdapter
    }

    private fun openReviewsActivityWithId(topicId: Int) {
        val intent = Intent(this, ReviewsActivity::class.java)
        intent.putExtra(TOPIC_ID_EXTRA, topicId)
        intent.putStringArrayListExtra(TOPIC_NAMES_EXTRA, topicItemsList)
        startActivity(intent)
    }

    fun showItems(topicItemsList: MutableList<TopicItem>) {
        for (topic in topicItemsList) {
            this.topicItemsList.add(topic.topic)
        }
        topicsAdapter.updateTopics(topicItemsList)
    }


}

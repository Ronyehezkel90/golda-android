package com.example.golda.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.administration.AdministrationPresenter
import com.example.golda.model.TopicItem
import kotlinx.android.synthetic.main.fragment_topics.*

class TopicsFragment : Fragment() {
    lateinit var topicsAdapter: TopicsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_topics, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        topicsRecyclerView.layoutManager = LinearLayoutManager(activity)
        topicsRecyclerView.adapter = this.topicsAdapter
        if ((activity as ReviewsActivity).isManager) {
            send_button.visibility = View.GONE
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun showTopics(topicItemsList: MutableList<TopicItem>) {
        topicsAdapter =
            TopicsAdapter { position -> (activity as ReviewsActivity).goToTopic(position) }
        topicsAdapter.updateTopics(topicItemsList)
    }
}
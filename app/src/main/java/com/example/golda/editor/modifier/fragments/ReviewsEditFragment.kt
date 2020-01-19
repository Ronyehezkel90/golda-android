package com.example.golda.editor.modifier.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.editor.modifier.ModifierActivity
import com.example.golda.editor.modifier.ModifierAdapter
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import kotlinx.android.synthetic.main.fragment_edit_users.*


class ReviewsEditFragment : Fragment() {

    val reviewsModifyAdapter: ModifierAdapter =
        ModifierAdapter("review")
    var topicsList = mutableListOf<TopicItem>()
    var reviewsList = mutableListOf<ReviewItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_users, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_item_button.setOnClickListener {
            addButtonClick()
        }
        remove_item_button.setOnClickListener {
            removeButtonClick()
        }
        editor_recycler_view.layoutManager = LinearLayoutManager(activity)
        editor_recycler_view.adapter = reviewsModifyAdapter

        first_edit_text.hint = "title"
        second_edit_text.hint = "subtitle"

        (activity as ModifierActivity).presenter.getTopics("reviewsFragment")
        (activity as ModifierActivity).presenter.getReviews()
    }

    private fun removeButtonClick() {
        val reviewItem = reviewsModifyAdapter.chosenItem as ReviewItem
        (activity as ModifierActivity).presenter.removeReview(reviewItem._id)
            ?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getReviews()
            }

    }

    private fun addButtonClick() {
        if (first_edit_text.text.toString() != "") {
            (activity as ModifierActivity).presenter.addReview(
                first_edit_text.text.toString(),
                second_edit_text.text.toString(),
                topicsList[spinner_roles.selectedItemPosition]._id
            )
                ?.addOnSuccessListener {
                    (activity as ModifierActivity).presenter.getReviews()
                }
        }
    }

    fun updateSpinner(topicsList: MutableList<TopicItem>) {
        this.topicsList = topicsList
        val adapter = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        )
        adapter.clear()
        for (topic in topicsList) {
            adapter.add(topic.topic)
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_roles.adapter = adapter
        spinner_roles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateReviewsAdapterByTopic()
            }

        }
    }

    fun updateReviewsAdapterByTopic() {
        val reviewsListToShow = mutableListOf<Any>()
        if (!topicsList.isNullOrEmpty()) {
            for (review in reviewsList) {
                if (review.topic == topicsList[spinner_roles.selectedItemPosition]._id) {
                    reviewsListToShow.add(review)
                }
            }
            reviewsModifyAdapter.updateReviews(reviewsListToShow)
        }
    }

    fun updateReviews(reviewsList: MutableList<ReviewItem>) {
        this.reviewsList = reviewsList
        updateReviewsAdapterByTopic()
    }

}
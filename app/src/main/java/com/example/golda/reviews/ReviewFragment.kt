package com.example.golda.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.model.ReviewItem
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewFragment(
    val topicReviews: MutableList<ReviewItem>?,
    val topicName: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_reviews, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reviewsRecyclerView.layoutManager = LinearLayoutManager(activity)
        reviewsRecyclerView.adapter = ReviewsAdapter(
            { reviewPosition -> takePhoto(reviewPosition) },
            { reviewId, rank -> (activity as ReviewsActivity).updateReviewRank(reviewId, rank) })
        (reviewsRecyclerView.adapter as ReviewsAdapter).updateItems(this.topicReviews!!)
        sectionTitle.text = topicName
        super.onViewCreated(view, savedInstanceState)
    }

    private fun takePhoto(reviewPosition: Int) {
        (activity as ReviewsActivity).launchCameraWithPermissionCheck(this, reviewPosition)
    }


}
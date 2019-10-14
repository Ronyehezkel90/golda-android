package com.example.golda.reviews

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.dagger.App
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewsActivity : MvpActivity<ReviewsView, ReviewsPresenter>(), ReviewsView {


    lateinit var reviewsAdapter: ReviewsAdapter

    override fun createPresenter(): ReviewsPresenter {
        return App.getAppComponent(this).getReviewsComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        reviewsRecyclerView.layoutManager = LinearLayoutManager(this)
        reviewsAdapter = ReviewsAdapter()
        reviewsRecyclerView.adapter = reviewsAdapter
    }

    override fun showItems(reviewItemList: List<ReviewItem>) {
        reviewsAdapter.updateReviewItems(reviewItemList)
    }

}

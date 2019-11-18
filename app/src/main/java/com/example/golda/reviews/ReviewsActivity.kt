package com.example.golda.reviews

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.golda.R
import com.example.golda.dagger.App
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.review_fragment.*

class ReviewsActivity : MvpActivity<ReviewsView, ReviewsPresenter>(), ReviewsView {

    lateinit var reviewsAdapter: ReviewsAdapter
    private val NUM_PAGES = 5

    override fun createPresenter(): ReviewsPresenter {
        return App.getAppComponent(this).getReviewsComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_fragment)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        view_pager.adapter = pagerAdapter
        reviewsAdapter = ReviewsAdapter()
    }

    override fun showItems(reviewItemList: List<ReviewItem>) {
        reviewsAdapter.updateReviewItems(reviewItemList)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = ReviewFragment()
    }
}

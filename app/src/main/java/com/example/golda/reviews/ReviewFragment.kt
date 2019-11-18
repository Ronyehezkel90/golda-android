package com.example.golda.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_reviews, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        reviewsRecyclerView.layoutManager = LinearLayoutManager(activity)
        reviewsRecyclerView.adapter = (activity as ReviewsActivity).reviewsAdapter
        super.onViewCreated(view, savedInstanceState)
    }
}
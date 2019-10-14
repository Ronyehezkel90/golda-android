package com.example.golda.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.golda.R
import kotlinx.android.synthetic.main.review_item.view.*

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    var reviewItemList: List<ReviewItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviewItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = reviewItemList[position].title
        holder.subtitle.text = reviewItemList[position].subtitle
    }

    fun updateReviewItems(reviewItemList: List<ReviewItem>) {
        this.reviewItemList = reviewItemList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val subtitle = itemView.subtitleTextView
    }
}
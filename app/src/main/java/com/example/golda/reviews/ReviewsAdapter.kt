package com.example.golda.reviews

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.golda.R
import com.example.golda.model.ReviewItem
import kotlinx.android.synthetic.main.review_item.view.*
import org.bson.types.ObjectId

class ReviewsAdapter(
    val cameraOnClick: (Int) -> Unit,
    val ratingBarOnChange: (ObjectId, Int) -> Unit
) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    var reviewItemList: MutableList<ReviewItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ViewHolder(view)
    }

    fun updateItems(topicReviews: MutableList<ReviewItem>) {
        this.reviewItemList = topicReviews
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reviewItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = reviewItemList[position].title
        holder.subtitle.text = reviewItemList[position].subtitle
        holder.RatingBar.setOnRatingBarChangeListener { ratingBar, rank, b ->
            ratingBarOnChange(reviewItemList[position]._id, rank.toInt())
        }
        holder.cameraButton.setOnClickListener {
            cameraOnClick(position)
        }
        if (reviewItemList[position].imageBitmap != null) {
            holder.cameraButton.setImageBitmap(reviewItemList[position].imageBitmap)
        }
    }

    fun setImageToItem(itemPosition: Int, bitmap: Bitmap) {
        reviewItemList[itemPosition].imageBitmap = bitmap
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val subtitle = itemView.subtitleTextView
        val cameraButton = itemView.circle
        val RatingBar: RatingBar = itemView.rating_bar

    }
}
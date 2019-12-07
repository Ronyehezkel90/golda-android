package com.example.golda.reviews

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.golda.R
import com.example.golda.model.ReviewItem
import kotlinx.android.synthetic.main.review_item.view.*
import org.bson.types.ObjectId

class ReviewsAdapter(
    private val isManager: Boolean,
    val cameraOnClick: (Int) -> Unit,
    val ratingBarOnChange: (ObjectId, Int) -> Unit,
    val commentOnClick: (Int) -> Unit,
    val openImageGallery: (String) -> Unit

) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    var reviewItemList: MutableList<ReviewItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        view.rating_bar.setIsIndicator(isManager)
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
        holder.commentButton.setImageResource(
            if (reviewItemList[position].comment == "" || reviewItemList[position].comment == null)
                R.drawable.ic_empty_comment
            else
                R.drawable.ic_modified_comment
        )
        holder.ratingBar.rating = reviewItemList[position].rank.toFloat()
        holder.ratingBar.setOnRatingBarChangeListener { ratingBar, rank, b ->
            ratingBarOnChange(reviewItemList[position]._id, rank.toInt())
        }
        if (reviewItemList[position].imageUrl != "") {
            holder.cameraButton.load(reviewItemList[position].imageUrl)
        }
        holder.cameraButton.setOnClickListener {
            if (isManager) {
                if (reviewItemList[position].imageUrl != "") {
                    openImageGallery(reviewItemList[position].imageUrl)
                }
            } else {
                cameraOnClick(position)
            }
        }
        holder.commentButton.setOnClickListener {
            commentOnClick(position)
        }
        //todo: after we implement upload images to s3 we should remove imageBitmap and rely only on imageUrl
        if (reviewItemList[position].imageBitmap != null) {
            holder.cameraButton.setImageBitmap(reviewItemList[position].imageBitmap)
        }
    }

    fun setImageToItem(itemPosition: Int, bitmap: Bitmap) {
        reviewItemList[itemPosition].imageBitmap = bitmap
        notifyDataSetChanged()
    }

    fun setCommentToItem(itemPosition: Int, comment: String) {
        reviewItemList[itemPosition].comment = comment
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val subtitle = itemView.subtitleTextView
        val cameraButton = itemView.circle
        val ratingBar: RatingBar = itemView.rating_bar
        val commentButton: ImageView = itemView.comment_button
    }
}
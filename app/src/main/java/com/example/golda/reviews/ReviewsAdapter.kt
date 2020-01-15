package com.example.golda.reviews

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.golda.R
import com.example.golda.model.ReviewItem
import kotlinx.android.synthetic.main.review_item.view.*
import org.bson.types.ObjectId

class ReviewsAdapter(
    private val isManager: Boolean,
    val cameraOnClick: (ReviewItem) -> Unit,
    val ratingBarOnChange: (ObjectId, Int) -> Unit,
    val commentOnClick: (Int) -> Unit,
    val openImageGallery: (Bitmap) -> Unit,
    val downloadImageByKey: (ReviewItem) -> Unit

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
        holder.cameraButton.setOnClickListener {
            if (isManager) {
                if (reviewItemList[position].imageBitmap != null) {
                    openImageGallery(reviewItemList[position].imageBitmap!!)
                }
            } else {
                cameraOnClick(reviewItemList[position])
                holder.cameraButton.setImageResource(R.drawable.ic_down)

            }
        }
        holder.commentButton.setOnClickListener {
            commentOnClick(position)
        }
        if (reviewItemList[position].imageBitmap != null) {
            holder.cameraButton.setImageBitmap(reviewItemList[position].imageBitmap)
        }
        else if (isManager && reviewItemList[position].imageUrl != "") {
            if (!holder.startDownload) {
                downloadImageByKey(reviewItemList[position])
                holder.cameraButton.setImageResource(R.drawable.ic_down)
                holder.startDownload = true
            }
        }
//        else if (isManager && reviewItemList[position].imageUrl != "") {
//            holder.cameraButton.setImageResource(R.drawable.ic_down)
//            downloadImageByKey(reviewItemList[position].imageUrl)
//        }
    }

//    fun setImageKeyToItem(itemPosition: Int, imgKey: String) {
//        reviewItemList[itemPosition].imageUrl = imgKey
//        downloadImageByKey(reviewItemList[itemPosition].imageUrl)
//    }

    fun setCommentToItem(itemPosition: Int, comment: String) {
        reviewItemList[itemPosition].comment = comment
        notifyDataSetChanged()
    }

    fun showImage() {
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val subtitle = itemView.subtitleTextView
        val cameraButton = itemView.circle
        val ratingBar: RatingBar = itemView.rating_bar
        val commentButton: ImageView = itemView.comment_button
        var startDownload: Boolean = false
    }
}
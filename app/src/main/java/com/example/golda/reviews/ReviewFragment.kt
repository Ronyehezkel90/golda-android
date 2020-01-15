package com.example.golda.reviews

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.model.ReviewItem
import kotlinx.android.synthetic.main.activity_reviews.*
import android.webkit.WebView
import android.R.attr.bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


class ReviewFragment(
    val idx: Int,
    val topicReviews: MutableList<ReviewItem>?,
    val topicName: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_reviews, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviews_recycler_view.layoutManager = LinearLayoutManager(activity)
        reviews_recycler_view.adapter = ReviewsAdapter(
            (activity as ReviewsActivity).isManager,
            { reviewPosition -> takePhoto(reviewPosition) },
            { reviewId, rank -> (activity as ReviewsActivity).updateReviewRank(reviewId, rank) },
            { reviewPosition -> addComment(reviewPosition) },
            { imageUrl -> openImageGallery(imageUrl) },
            { imageKey -> downloadImageByKey(imageKey) }

        )
        (reviews_recycler_view.adapter as ReviewsAdapter).updateItems(this.topicReviews!!)
        section_title.text = topicName
        gallery_view_frame_layout.setOnClickListener {
            gallery_view_frame_layout.visibility = View.GONE
        }
    }

    private fun downloadImageByKey(reviewItemWithImage: ReviewItem) {
        (activity as ReviewsActivity).downloadImageByKey(reviewItemWithImage, this)
    }

    private fun openImageGallery(bitmap: Bitmap) {
        gallery_view_frame_layout.visibility = View.VISIBLE
//        gallery_view_image_view.setImageBitmap(bitmap)

        gallery_view_image_view.settings.builtInZoomControls = true
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        val dataURL = "data:image/png;base64,$imgageBase64"

        gallery_view_image_view.loadUrl(dataURL)
    }

    private fun addComment(reviewPosition: Int) {
        (activity as ReviewsActivity).addComment(this, reviewPosition)
    }

    private fun takePhoto(reviewItem: ReviewItem) {
        (activity as ReviewsActivity).launchCameraWithPermissionCheck(reviewItem)
    }
}
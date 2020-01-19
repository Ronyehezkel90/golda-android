package com.example.golda.reviews

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.golda.R
import com.example.golda.administration.AdministrationActivity.Companion.ADMINISTRATION_ROLE_EXTRA
import com.example.golda.administration.AdministrationActivity.Companion.BRANCH_ID_EXTRA
import com.example.golda.administration.AdministrationActivity.Companion.CHOSEN_DATE_EXTRA
import com.example.golda.administration.AdministrationPresenter.ROLE
import com.example.golda.dagger.App
import com.example.golda.model.TopicItem
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.reviews_view_pager.*
import org.bson.types.ObjectId
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber


@RuntimePermissions
class ReviewsActivity : MvpActivity<ReviewsView, ReviewsPresenter>(), ReviewsView {

    private val CAMERA_REQUEST_CODE: Int = 101
    private var itemPosition: Int = -1
    lateinit var topicsFragment: TopicsFragment
    private lateinit var fragmentCameraClicked: ReviewFragment
    private var topicId: Int = -1
    lateinit var topicItemsList: MutableList<TopicItem>
    lateinit var branchId: ObjectId
    lateinit var date: String
    var isManager: Boolean = false


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun createPresenter(): ReviewsPresenter {
        return App.getAppComponent(this).getReviewsComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reviews_view_pager)
        isManager = intent.getSerializableExtra(ADMINISTRATION_ROLE_EXTRA) as ROLE == ROLE.MANAGER
        branchId = intent.getSerializableExtra(BRANCH_ID_EXTRA) as ObjectId
        if (isManager) {
            date = intent.getStringExtra(CHOSEN_DATE_EXTRA)
            presenter.displayResultReviews(branchId, date)
        } else {
            presenter.displayReviews()
        }
    }

    override fun createAdapter(topicItemsList: MutableList<TopicItem>) {
        reviews_view_pager.adapter = ScreenSlidePagerAdapter(this, topicItemsList)
        reviews_view_pager.currentItem = topicId
    }

    private fun getFragmentAdapter(reviewFragment: ReviewFragment): ReviewsAdapter {
        return (reviewFragment.reviews_recycler_view.adapter as ReviewsAdapter)
    }

    override fun addComment(reviewFragment: ReviewFragment, reviewPosition: Int, topicId:ObjectId) {
        val editText = EditText(this)
        editText.setText(
            presenter.topicReviewsMap[topicId]?.get(reviewPosition)?.comment
        )
        editText.isEnabled = !isManager
        val alert = AlertDialog.Builder(this)
        alert.setTitle(if (isManager) "Comment" else "Add comment")
        alert.setView(editText)
        if (isManager) {
            alert.setPositiveButton("Ok") { dialog, whichButton -> }
        } else {
            alert.setPositiveButton("Add") { dialog, whichButton ->
                getFragmentAdapter(reviewFragment).setCommentToItem(
                    reviewPosition,
                    editText.text.toString()
                )
            }
            alert.setNegativeButton("Cancel") { dialog, whichButton -> }
        }
        alert.show()
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun launchCamera(fragment: ReviewFragment, position: Int) {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        itemPosition = position
        fragmentCameraClicked = fragment
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied() {
        Toast.makeText(this, "Cant take picture because permission denied", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Timber.d("picture ok")
                    if (itemPosition != -1) {
                        getFragmentAdapter(fragmentCameraClicked).setImageToItem(
                            itemPosition,
                            data.extras!!.get("data") as Bitmap
                        )
                        itemPosition = -1
                    }
                }
            }
            else -> {
                Timber.d("picture failed")
//                Toast.makeText(this,"Unrecognized request code",Toast.LENGTH_SHORT)
            }
        }
    }

    fun updateReviewRank(reviewId: ObjectId, rank: Int) {
        presenter.updateReviewItemRank(reviewId, rank)
    }

    override fun goToTopic(pos: Int) {
        reviews_view_pager.currentItem = pos + 1
    }

    override fun showTopics(topicItemsList: MutableList<TopicItem>) {
        this.topicItemsList = topicItemsList
    }

    override fun onBackPressed() {
        if (reviews_view_pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            reviews_view_pager.currentItem = 0
        }
    }

    private inner class ScreenSlidePagerAdapter(
        fa: FragmentActivity,
        val topicItemsList: MutableList<TopicItem>
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = topicItemsList.size + 1

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                topicsFragment = TopicsFragment()
                topicsFragment.showTopics(topicItemsList)
                return topicsFragment
            } else {
                val topicReviewsList = presenter.topicReviewsMap[topicItemsList[position-1]._id]
                val reviewItemsList =
                    if (topicReviewsList.isNullOrEmpty()) mutableListOf() else topicReviewsList
                ReviewFragment(
                    position - 1,
                    reviewItemsList,
                    topicItemsList[position - 1].topic
                )
            }
        }
    }

    fun sendButtonClicked(view: View) {
        presenter.sendReview(branchId)
    }

    fun outsideGalleryViewClicked(view: View) {
        gallery_view_frame_layout.visibility = View.GONE
    }

}

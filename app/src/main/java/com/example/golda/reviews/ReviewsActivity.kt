package com.example.golda.reviews

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
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
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.reviews_view_pager.*
import org.bson.types.ObjectId
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import java.io.File


@RuntimePermissions
class ReviewsActivity : MvpActivity<ReviewsView, ReviewsPresenter>(), ReviewsView {

    private val CAMERA_REQUEST_CODE: Int = 101
    lateinit var topicsFragment: TopicsFragment
    private var topicId: Int = -1
    lateinit var topicItemsList: MutableList<TopicItem>
    var isManager: Boolean = false
    val fragments = mutableListOf<ReviewFragment>()
    lateinit var currentReviewItem: ReviewItem

    companion object {
        var imgFilePath: String = "/sdcard/%s"
    }

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
        presenter.branchId = intent.getSerializableExtra(BRANCH_ID_EXTRA) as ObjectId
        presenter.date = intent.getStringExtra(CHOSEN_DATE_EXTRA)
        if (isManager) {
            presenter.displayResultReviews(presenter.branchId, presenter.date)
        } else {
            presenter.displayReviews()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isManager) {
            setSpinnerVisibility(true)
        }
    }

    override fun setSpinnerVisibility(isVisible: Boolean) {
        if (isVisible) {
            activity_progress_bar.visibility = View.VISIBLE
        } else {
            activity_progress_bar.visibility = View.GONE
        }
    }

    override fun createAdapter(topicItemsList: MutableList<TopicItem>) {
        this.topicItemsList = topicItemsList
        reviews_view_pager.adapter = ScreenSlidePagerAdapter(this, topicItemsList)
        reviews_view_pager.currentItem = topicId
    }

    override fun getFragmentAdapter(reviewFragment: ReviewFragment): ReviewsAdapter {
        return (reviewFragment.reviews_recycler_view.adapter as ReviewsAdapter)
    }

    override fun addComment(
        reviewFragment: ReviewFragment,
        reviewPosition: Int,
        topicId: ObjectId
    ) {
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

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun launchCamera(reviewItem: ReviewItem) {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val imgKey = presenter.getImgKey(reviewItem._id)
        reviewItem.imageUrl = imgKey
        currentReviewItem = reviewItem

        val uriSavedImage = Uri.fromFile(File(imgFilePath.format(imgKey)))
        callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
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
                if (resultCode == Activity.RESULT_OK) {
                    val imageBitmap = BitmapFactory.decodeFile(
                        imgFilePath.format(
                            presenter.getImgKey(currentReviewItem._id)
                        )
                    )
                    currentReviewItem.imageBitmap = imageBitmap
                    updateItems(currentReviewItem.topic)
                }
            }
            else -> {
                Timber.d("picture failed")
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

    override fun downloadImageByKey(
        reviewItemWithImage: ReviewItem,
        reviewFragment: ReviewFragment
    ) {
        return presenter.downloadImage(reviewItemWithImage)
    }

    fun getFragmentIdx(topicId: ObjectId): Int {
        for ((idx, topic) in topicItemsList.withIndex()) {
            if (topic._id == topicId) {
                return idx
            }
        }
        return -1
    }

    override fun updateItems(topicId: ObjectId) {
        getFragmentAdapter(fragments[getFragmentIdx(topicId)]).updateItems(presenter.topicReviewsMap[topicId]!!)
    }

    fun sendButtonClicked(view: View) {
        setLoaderVisibility(true)
        presenter.sendReview(presenter.branchId)
    }

    override fun setLoaderVisibility(showLoader: Boolean) {
        send_button.isClickable = !showLoader
        loader_spinner.visibility = if (showLoader) View.VISIBLE else View.GONE
    }

    override fun closeActivity() {
        finish()
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
                val topicReviewsList = presenter.topicReviewsMap[topicItemsList[position - 1]._id]
                val reviewItemsList =
                    if (topicReviewsList.isNullOrEmpty()) mutableListOf() else topicReviewsList
                val revFragment = ReviewFragment(
                    position - 1,
                    reviewItemsList,
                    topicItemsList[position - 1].topic
                )
                fragments.add(revFragment)
                return revFragment
            }
        }
    }

}

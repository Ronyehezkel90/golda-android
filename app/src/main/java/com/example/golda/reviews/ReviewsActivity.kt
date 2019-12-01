package com.example.golda.reviews

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.golda.R
import com.example.golda.dagger.App
import com.example.golda.topics.TopicsActivity.Companion.TOPIC_ID_EXTRA
import com.example.golda.topics.TopicsActivity.Companion.TOPIC_NAMES_EXTRA
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.review_fragment.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
class ReviewsActivity : MvpActivity<ReviewsView, ReviewsPresenter>(), ReviewsView {

    private var topicCount: Int = -1
    private val CAMERA_REQUEST_CODE: Int = 101
    private var itemPosition: Int = -1
    private lateinit var fragmentCameraClicked: ReviewFragment
    private lateinit var topicNames: ArrayList<String>
    private var topicId: Int = -1

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
        setContentView(R.layout.review_fragment)
        topicId = intent.getIntExtra(TOPIC_ID_EXTRA, 0)
        topicNames = intent.getStringArrayListExtra(TOPIC_NAMES_EXTRA)
        topicCount = topicNames.size
    }

    override fun createAdapter() {
        topic_view_pager.adapter = ScreenSlidePagerAdapter(this, topicCount)
        topic_view_pager.currentItem = topicId
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
                        (fragmentCameraClicked.reviewsRecyclerView.adapter as ReviewsAdapter).setImageToItem(
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

    private inner class ScreenSlidePagerAdapter(
        fa: FragmentActivity,
        val topicCount: Int
    ) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = topicCount

        override fun createFragment(position: Int): ReviewFragment {
            return ReviewFragment(presenter.topicReviewsMap[position], topicNames[position])
        }
    }
}

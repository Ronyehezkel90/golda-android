package com.example.golda.reviews

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Environment
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.example.golda.MongoManager
import com.example.golda.Repository
import com.example.golda.S3Manager
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.example.golda.reviews.ReviewsActivity.Companion.imgFilePath
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import org.bson.Document
import org.bson.types.ObjectId
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ReviewsPresenter
@Inject constructor(
    private val s3Manager: S3Manager,
    private val mongoManager: MongoManager,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences,
    private val repository: Repository

) : MvpNullObjectBasePresenter<ReviewsView>() {

    lateinit var branchId: ObjectId
    lateinit var date: String
    private var reviewsNumber = 0
    val topicReviewsMap = mutableMapOf<Int, MutableList<ReviewItem>>()

    fun displayResultReviews(branchId: ObjectId, date: String) {
        mongoManager.getReviews().addOnSuccessListener {
            reviewsNumber = it.size
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                downloadImage(reviewItem)
                if (reviewItem.topic !in topicReviewsMap) {
                    topicReviewsMap[reviewItem.topic] = ArrayList()
                }
                topicReviewsMap[reviewItem.topic]!!.add(reviewItem)
            }
            rankReviews(branchId, date)
        }
    }

    private fun rankReviews(branchId: ObjectId, date: String) {
        mongoManager.getReviewsRelationsByBranchAndDate(branchId, date).addOnSuccessListener {
            mongoManager.getReviewsResultsById(it[0]["reviewResultId"] as ObjectId)
                .addOnSuccessListener {
                    (it[0]["topics"] as ArrayList<*>).forEach {
                        //                        for (review in topicReviewsMap[(it as Document).get("topic_id")] as ArrayList<ReviewItem>){
                        for (reviewDoc in (it as Document)["reviews"] as ArrayList<Document>) {
                            val review =
                                findReviewInTopicReviewsMapById(reviewDoc["review_id"] as ObjectId)
                            review?.rank = reviewDoc["rank"] as Int
                            review?.comment =
                                if (reviewDoc["comment"] == null) "" else reviewDoc["comment"] as String
                            review?.imageUrl =
                                if (reviewDoc["image_url"] == null) "" else reviewDoc["image_url"] as String
                        }
                    }
                    displayTopics()
                }
        }
    }

    private fun findReviewInTopicReviewsMapById(reviewObjectId: ObjectId): ReviewItem? {
        for (reviewsByTopic in topicReviewsMap.values) {
            for (review in reviewsByTopic) {
                if (review._id == reviewObjectId) {
                    return review
                }
            }
        }
        return null
    }

    fun displayReviews() {
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                if (reviewItem.topic !in topicReviewsMap) {
                    topicReviewsMap[reviewItem.topic] = ArrayList()
                }
                topicReviewsMap[reviewItem.topic]!!.add(reviewItem)
            }
            displayTopics()
        }
    }

    fun updateReviewItemRank(reviewId: ObjectId, rank: Int) {
        for (topic in topicReviewsMap) {
            for (review: ReviewItem in topic.value) {
                if (review._id == reviewId) {
                    review.rank = rank
                }
            }
        }
    }

    private fun displayTopics() {
        val topicItemsList = mutableListOf<TopicItem>()
        mongoManager.getTopics().addOnSuccessListener {
            it.forEach {
                topicItemsList.add(gson.fromJson(it.toJson(), TopicItem::class.java))
            }
            topicItemsList.sortBy { it.id }
            view.createAdapter(topicItemsList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun sendReview(branchId: ObjectId) {

        for (topic in topicReviewsMap.values) {
            for (review in topic)
                if (review.imageUrl != null)
                    s3Manager.uploadImage(
                        File(imgFilePath.format(review.imageUrl)),
                        review.imageUrl
                    )
        }

        val reviewerId = ObjectId(sharedPreferences.getString("userId", "no user id"))
        mongoManager.sendReview(topicReviewsMap)?.addOnSuccessListener {
            Timber.d("review inserted successfully")
            mongoManager.insertMediatorDoc(it.insertedId, branchId, reviewerId)
                ?.addOnSuccessListener {
                    view.setLoaderVisibility(false)
                    view.closeActivity()
                }
                ?.addOnFailureListener {
                    view.setLoaderVisibility(false)
                }
        }?.addOnFailureListener { Timber.d("review insert failed") }

    }

    fun getImgKey(revId: ObjectId?): String {
        return "$branchId-${date.replace('/', '-')}-$revId.png"
    }

    private fun getTopicPositionByKey(imageKey: String): Int {
        val revId = imageKey.substring(imageKey.findLastAnyOf(listOf("-"))?.first as Int + 1)
        for ((j, reviewsByTopic) in topicReviewsMap.values.withIndex()) {
            for ((i, review) in reviewsByTopic.withIndex()) {
                if (review._id.toString() + ".png" == revId) {
                    return j
                }
            }
        }
        return -1
    }

    private fun getItemPositionByKey(imageKey: String): Int {
        val revId = imageKey.substring(imageKey.findLastAnyOf(listOf("-"))?.first as Int + 1)
        for (reviewsByTopic in topicReviewsMap.values) {
            for ((i, review) in reviewsByTopic.withIndex()) {
                if (review._id.toString() + ".png" == revId) {
                    return i
                }
            }
        }
        return -1
    }

    fun uploadImage(file: File, reviewItemWithImage: ReviewItem) {
//        val transferNetworkLossHandler:TransferNetworkLossHandler = TransferNetworkLossHandler.getInstance(view as Context)
        s3Manager.uploadImage(file, reviewItemWithImage.imageUrl)?.setTransferListener(
            object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    Timber.d("onStateChanged")
                    if (state == TransferState.COMPLETED) {
                        val imageFile =
                            File(Environment.getExternalStorageDirectory(), "temp_image.jpg").path
                        val imageBitmap = BitmapFactory.decodeFile(imageFile)
                        reviewItemWithImage.imageBitmap = imageBitmap
                    } else if (state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK) {
                        Timber.d("upload failed")
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Timber.d("onProgressChanged")
                }

                override fun onError(id: Int, ex: Exception) {
                    Timber.d("Error")
                }
            })
    }

    fun downloadImage(reviewItemWithImage: ReviewItem) {
        s3Manager.downloadPic(reviewItemWithImage.imageUrl)
            ?.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED == state) {
                        val imageFile = File(
                            Environment.getExternalStorageDirectory(),
                            reviewItemWithImage.imageUrl
                        ).path
                        val imageBitmap = BitmapFactory.decodeFile(imageFile)
                        reviewItemWithImage.imageBitmap = imageBitmap
                        reduceReviewsAndTryToContinue()
                    }
                }

                private fun reduceReviewsAndTryToContinue() {
                    reviewsNumber--
                    if (reviewsNumber == 0) {
                        view.setLoaderVisibility(showLoader = false)
                    }
                }

                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    try {
                        val done = (((current.toDouble() / total) * 100.0).toInt()) //as Int
                        Timber.d("DOWNLOAD - - ID: $id, percent done = $done")
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }

                override fun onError(id: Int, ex: Exception) {
                    Timber.e("DOWNLOAD ERROR - - ID: $id - - EX: ${ex.message.toString()}")
                    reduceReviewsAndTryToContinue()
                }
            })
    }
}

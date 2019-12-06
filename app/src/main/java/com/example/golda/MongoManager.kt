package com.example.golda

import android.content.SharedPreferences
import com.example.golda.model.ReviewItem
import com.google.android.gms.tasks.Task
import com.jakewharton.rxrelay2.BehaviorRelay
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import org.bson.BsonDocument
import org.bson.BsonObjectId
import org.bson.BsonValue
import org.bson.Document
import org.bson.types.ObjectId
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MongoManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val stitchAppClient: StitchAppClient
) {

    val mongoDb: RemoteMongoDatabase
    val mongoConnectionBehaviorRelay: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)

    init {
        val mongoClient =
            stitchAppClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        if (sharedPreferences.contains("mongoId")) {
            mongoConnectionBehaviorRelay.accept(true)
        } else {
            stitchAppClient.auth.loginWithCredential(AnonymousCredential())
                .addOnSuccessListener {
                    Timber.d("mongo success")
                    val editor = sharedPreferences.edit()
                    editor.putString("mongoId", stitchAppClient.auth.user!!.id)
                    editor.apply()
                    mongoConnectionBehaviorRelay.accept(true)
                }
                .addOnFailureListener {
                    Timber.d("mongo fail")
                }
        }
        mongoDb = mongoClient.getDatabase("golda")
    }

    fun observeMongoConnection(): BehaviorRelay<Boolean> {
        return mongoConnectionBehaviorRelay
    }

    fun getReviews(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("reviewItems")
        return reviewItemsCollection.find().into(result)
    }

    fun getTopics(): Task<MutableList<Document>> {
        val topics = mutableListOf<Document>()
        val topicsItemsCollection = mongoDb.getCollection("topicItems")
        return topicsItemsCollection.find().into(topics)
    }

    fun getUsers(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("users")
        return reviewItemsCollection.find().into(result)

    }

    fun getBranches(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("branches")
        return reviewItemsCollection.find().into(result)

    }

    fun getReviewsRelationsByBranch(branchId: ObjectId): Task<MutableList<Document>> {
        val query = BsonDocument("branchId", BsonObjectId(branchId))
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("reviewMediator")
        return reviewItemsCollection.find(query).into(result)
    }

    fun sendReview(
        topicReviewsMap: MutableMap<Int, MutableList<ReviewItem>>,
        branchId: ObjectId,
        reviewerId: ObjectId
    ) {
        val document = Document()
        val topicsList = arrayListOf<MutableMap<String, Any>>()
        for (topic in topicReviewsMap) {
            val topicObject = mutableMapOf<String, Any>()
            topicObject["topic_id"] = topic.key
            val reviewsList = arrayListOf<MutableMap<String, Any>>()
            for (reviewItem in topicReviewsMap[topic.key]!!) {
                val reviewResultMap = mutableMapOf<String, Any>()
                reviewResultMap["review_id"] = reviewItem._id
                reviewResultMap["image_url"] = reviewItem.imageUrl
                reviewResultMap["rank"] = reviewItem.rank
                reviewResultMap["comment"] = reviewItem.comment
                reviewsList.add(reviewResultMap)
            }
            topicObject["reviews"] = reviewsList
            topicsList.add(topicObject)
            document.append("topics", topicsList)
        }
        val reviewResultItemsCollection = mongoDb.getCollection("reviewsResults")
        reviewResultItemsCollection.insertOne(document)
            .addOnSuccessListener {
                Timber.d("review inserted successfully")
                insertMediatorDoc(it.insertedId, branchId, reviewerId)
            }
            .addOnFailureListener { Timber.d("review insert failed") }

    }

    private fun insertMediatorDoc(
        reviewResultId: BsonValue,
        branchId: ObjectId,
        reviewerId: ObjectId
    ) {
        val reviewMediatorCollection = mongoDb.getCollection("reviewMediator")
        val document = Document()
        document.append("reviewResultId", reviewResultId)
        document.append("branchId", branchId)
        document.append("reviewerId", reviewerId)
        document.append("date", Date())
        reviewMediatorCollection.insertOne(document)

        return
    }

}
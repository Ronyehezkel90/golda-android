package com.example.golda

import android.content.SharedPreferences
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.example.golda.model.UserItem
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.jakewharton.rxrelay2.BehaviorRelay
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult
import org.bson.*
import org.bson.types.ObjectId
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MongoManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val stitchAppClient: StitchAppClient,
    private val gson: Gson
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

    fun getReviewsRelationsByBranchAndDate(
        branchId: ObjectId,
        date: String
    ): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("reviewMediator")
        val query = BsonDocument("branchId", BsonObjectId(branchId))
        query["date"] = BsonString(date)
        return reviewItemsCollection.find(query).into(result)
    }

    fun getReviewsResultsById(reviewResultsId: ObjectId): Task<MutableList<Document>> {
        val reviewResultItemsCollection = mongoDb.getCollection("reviewsResults")
        val result = mutableListOf<Document>()
        val query = BsonDocument("_id", BsonObjectId(reviewResultsId))
        return reviewResultItemsCollection.find(query).into(result)
    }

    fun getTopics(): Task<MutableList<Document>> {
        val topics = mutableListOf<Document>()
        val topicsItemsCollection = mongoDb.getCollection("topicItems")
        return topicsItemsCollection.find().into(topics)
    }

    fun getUsers(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val usersItemsCollection = mongoDb.getCollection("users")
        return usersItemsCollection.find().into(result)

    }

    fun getBranches(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("branches")
        return reviewItemsCollection.find().into(result)
    }

    fun getReviewsRelationsByBranch(branchId: ObjectId): Task<MutableList<Document>> {
        val reviewItemsCollection = mongoDb.getCollection("reviewMediator")
        val query = BsonDocument("branchId", BsonObjectId(branchId))
        val result = mutableListOf<Document>()
        return reviewItemsCollection.find(query).into(result)
    }

    fun sendReview(
        topicReviewsMap: MutableMap<ObjectId, MutableList<ReviewItem>>
    ): Task<RemoteInsertOneResult>? {
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
        return reviewResultItemsCollection.insertOne(document)


    }

    fun insertMediatorDoc(
        reviewResultId: BsonValue,
        branchId: ObjectId,
        reviewerId: ObjectId
    ): Task<RemoteInsertOneResult>? {
        val reviewMediatorCollection = mongoDb.getCollection("reviewMediator")
        val document = Document()
        document.append("reviewResultId", reviewResultId)
        document.append("branchId", branchId)
        document.append("reviewerId", reviewerId)
        document.append("date", SimpleDateFormat("dd/MM/yyyy").format(Date()))
        return reviewMediatorCollection.insertOne(document)
    }

    fun addUser(userItem: UserItem): Task<RemoteInsertOneResult>? {
        val usersItemsCollection = mongoDb.getCollection("users")
        val document = Document()
        document.append("_id", userItem._id)
        document.append("name", userItem.name)
        document.append("password", userItem.password)
        document.append("role", userItem.role)
        return usersItemsCollection.insertOne(document)
    }

    fun removeUser(userId: ObjectId): Task<RemoteDeleteResult>? {
        val usersItemsCollection = mongoDb.getCollection("users")
        return usersItemsCollection.deleteOne(BsonDocument("_id", BsonObjectId(userId)))
    }

    fun removeTopic(topicId: ObjectId): Task<RemoteDeleteResult>? {
        val usersItemsCollection = mongoDb.getCollection("topicItems")
        return usersItemsCollection.deleteOne(BsonDocument("_id", BsonObjectId(topicId)))
    }

    fun addTopic(topicItem: TopicItem): Task<RemoteInsertOneResult>? {
        val topicItemsCollection = mongoDb.getCollection("topicItems")
        val document = Document()
        document.append("_id", topicItem._id)
        document.append("topic", topicItem.topic)
        return topicItemsCollection.insertOne(document)
    }

    fun removeReview(reviewId: ObjectId): Task<RemoteDeleteResult>? {
        val usersItemsCollection = mongoDb.getCollection("reviewItems")
        return usersItemsCollection.deleteOne(BsonDocument("_id", BsonObjectId(reviewId)))
    }

    fun addReview(
        reviewId: ObjectId,
        title: String,
        subtitle: String,
        topicId: ObjectId
    ): Task<RemoteInsertOneResult>? {
        val reviewItemsCollection = mongoDb.getCollection("reviewItems")
        val document = Document()
        document.append("_id", reviewId)
        document.append("title", title)
        document.append("subtitle", subtitle)
        document.append("topic", topicId)
        return reviewItemsCollection.insertOne(document)
    }

    fun addBranch(
        objectId: ObjectId,
        name: String,
        address: String,
        phone: String,
        managerObjectId: ObjectId
    ): Task<RemoteInsertOneResult>? {
        val branchItemsCollection = mongoDb.getCollection("branches")
        val document = Document()
        document.append("_id", objectId)
        document.append("name", name)
        document.append("address", address)
        document.append("phone", phone)
        document.append("managerId", managerObjectId)
        return branchItemsCollection.insertOne(document)
    }

    fun removeBranch(branchId: ObjectId): Task<RemoteDeleteResult>? {
        val branchItemsCollection = mongoDb.getCollection("branches")
        return branchItemsCollection.deleteOne(BsonDocument("_id", BsonObjectId(branchId)))
    }

}
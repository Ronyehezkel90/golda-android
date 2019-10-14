package com.example.golda

import android.content.Context
import android.content.SharedPreferences
import com.example.golda.reviews.ReviewItem
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import org.bson.Document
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MongoManager @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val mongoCollection: RemoteMongoCollection<Document>,
    private val gson: Gson
) {

    fun getReviews(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        return mongoCollection.find().into(result)
    }

//    fun insert(stitchAppClient: RemoteMongoCollection<Document>) {
//        val reviewItem = Document()
//        reviewItem["title"] = "title"
//        reviewItem["subtitle"] = "subtitle"
//        reviewItem["id"] = sharedPreferences.getString("mongoId", "0")
//        myCollection.insertOne(reviewItem)
//            .addOnSuccessListener {
//                Timber.d("One document inserted")
//            }
//            .addOnFailureListener {
//                Timber.e(it)
//            }
//    }

}
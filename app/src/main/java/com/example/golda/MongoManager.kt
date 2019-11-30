package com.example.golda

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.jakewharton.rxrelay2.BehaviorRelay
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import io.reactivex.disposables.Disposable
import org.bson.Document
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MongoManager @Inject constructor(
    private val context: Context,
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

    fun getUsers(): Task<MutableList<Document>> {
        val result = mutableListOf<Document>()
        val reviewItemsCollection = mongoDb.getCollection("users")
        return reviewItemsCollection.find().into(result)

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
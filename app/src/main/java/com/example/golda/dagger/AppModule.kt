package com.example.golda.dagger

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import dagger.Module
import dagger.Provides
import org.bson.Document
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    internal fun provideContext(): Context {
        return context
    }

    @Provides
    internal fun provideGson(): Gson {
        return Gson()
    }

//    @Provides
//    internal fun provideRetrofit(): RetrofitService {
//        val BASE_URL = "https://jsonplaceholder.typicode.com"
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build().create(RetrofitService::class.java)
//    }


    @Singleton
    @Provides
    internal fun provideSharedPref(): SharedPreferences {
        return context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    internal fun provideMongoCollection(sharedPreference: SharedPreferences): RemoteMongoCollection<Document> {
        Stitch.initializeDefaultAppClient("golda-dxwyb")
        val stitchAppClient = Stitch.getDefaultAppClient()
        val mongoClient =
            stitchAppClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        if (!sharedPreference.contains("mongoId")) {
            val editor = sharedPreference.edit()
            editor.putString("mongoId", stitchAppClient.auth.user!!.id)
            editor.apply()
        }
        return mongoClient.getDatabase("golda").getCollection("reviewItems")

    }

}
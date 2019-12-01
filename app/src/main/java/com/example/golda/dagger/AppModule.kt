package com.example.golda.dagger

import android.content.Context
import android.content.SharedPreferences
import com.example.golda.R
import com.google.gson.Gson
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import dagger.Module
import dagger.Provides
import org.bson.Document
import timber.log.Timber
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

    @Singleton
    @Provides
    internal fun provideSharedPref(): SharedPreferences {
        return context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    internal fun provideMongoClient(): StitchAppClient {
        return Stitch.initializeDefaultAppClient("golda-dxwyb")
    }

}
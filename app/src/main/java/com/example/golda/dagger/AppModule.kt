package com.example.golda.dagger

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.*
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.StitchAppClient
import dagger.Module
import dagger.Provides
import org.bson.types.ObjectId
import java.lang.reflect.Type
import java.util.*
import javax.inject.Singleton


@Module
class AppModule(private val context: Context) {

    @Provides
    internal fun provideContext(): Context {
        return context
    }

    inner class JsonDateDeserializer : JsonDeserializer<Date> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Date {
            val s = json.asJsonObject.get("\$date").asString
//            val l = java.lang.Long.parseLong(s.substring(6, s.length - 2))
            val l = java.lang.Long.parseLong(s)
            return Date(l)
        }
    }

    @Provides
    internal fun provideGson(): Gson {
        val des: JsonDeserializer<ObjectId> =
            JsonDeserializer { je, type, jdc -> ObjectId(je.asJsonObject.get("\$oid").asString) }
        return GsonBuilder()
            .registerTypeAdapter(ObjectId::class.java, des)
//            .registerTypeAdapter(Date::class.java, JsonDateDeserializer())
            .create()
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
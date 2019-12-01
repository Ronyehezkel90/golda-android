package com.example.golda.dagger

import com.example.golda.MainComponent
import com.example.golda.reviews.ReviewsComponent
import com.example.golda.topics.TopicsComponent
import com.google.gson.Gson
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun getReviewsComponent(): ReviewsComponent

    fun getMainComponent(): MainComponent

    fun getTopicsComponent(): TopicsComponent

    fun getGson(): Gson
}
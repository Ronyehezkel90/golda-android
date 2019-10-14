package com.example.golda.dagger

import com.example.golda.reviews.ReviewsComponent
import com.google.gson.Gson
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun getReviewsComponent(): ReviewsComponent

    fun getMediator(): Mediator

    fun getGson():Gson

}
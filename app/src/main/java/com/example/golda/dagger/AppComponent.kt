package com.example.golda.dagger

import com.example.golda.sign.SignComponent
import com.example.golda.administration.AdministrationComponent
import com.example.golda.reviews.ReviewsComponent
import com.google.gson.Gson
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun getReviewsComponent(): ReviewsComponent

    fun getManagerComponent(): AdministrationComponent

    fun getMainComponent(): SignComponent

    fun getGson(): Gson
}
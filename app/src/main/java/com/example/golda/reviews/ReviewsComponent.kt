package com.example.golda.reviews

import dagger.Subcomponent

@Subcomponent
interface ReviewsComponent {
    val presenter: ReviewsPresenter
}